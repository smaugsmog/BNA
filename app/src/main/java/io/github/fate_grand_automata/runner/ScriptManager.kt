package io.github.fate_grand_automata.runner

import android.annotation.SuppressLint
import android.app.Service
import android.content.ClipboardManager
import android.content.Context
import android.content.DialogInterface
import android.widget.Toast
import dagger.hilt.EntryPoints
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped
import io.github.fate_grand_automata.R
import io.github.fate_grand_automata.accessibility.TapperService
import io.github.fate_grand_automata.di.script.ScriptComponentBuilder
import io.github.fate_grand_automata.di.script.ScriptEntryPoint
import io.github.fate_grand_automata.di.service.ServiceCoroutineScope
import io.github.fate_grand_automata.prefs.core.PrefsCore
import io.github.fate_grand_automata.scripts.entrypoints.AutoBattle
import io.github.fate_grand_automata.scripts.entrypoints.AutoCEBomb
import io.github.fate_grand_automata.scripts.entrypoints.AutoFriendGacha
import io.github.fate_grand_automata.scripts.entrypoints.AutoGiftBox
import io.github.fate_grand_automata.scripts.entrypoints.AutoLottery
import io.github.fate_grand_automata.scripts.entrypoints.AutoServantLevel
import io.github.fate_grand_automata.scripts.entrypoints.SupportImageMaker
import io.github.fate_grand_automata.scripts.bna.entrypoints.Dailies
import io.github.fate_grand_automata.scripts.bna.entrypoints.Metaspace
import io.github.fate_grand_automata.scripts.bna.entrypoints.Map
import io.github.fate_grand_automata.scripts.bna.entrypoints.VoidMirror
import io.github.fate_grand_automata.ui.launcher.BnaScript
import io.github.fate_grand_automata.ui.launcher.BnaScriptLauncher
import io.github.fate_grand_automata.scripts.enums.GameServer
import io.github.fate_grand_automata.scripts.enums.ScriptModeEnum
import io.github.fate_grand_automata.scripts.prefs.IPreferences
import io.github.fate_grand_automata.ui.exit.BattleExit
import io.github.fate_grand_automata.ui.launcher.ScriptLauncher
import io.github.fate_grand_automata.ui.launcher.ScriptLauncherResponse
import io.github.fate_grand_automata.ui.runner.ScriptRunnerUIState
import io.github.fate_grand_automata.ui.runner.ScriptRunnerUIStateHolder
import io.github.fate_grand_automata.ui.support_img_namer.showSupportImageNamer
import io.github.fate_grand_automata.util.FakedComposeView
import io.github.fate_grand_automata.util.ImageLoader
import io.github.fate_grand_automata.util.KnownException
import io.github.fate_grand_automata.util.ScriptMessages
import io.github.fate_grand_automata.util.ScriptState
import io.github.fate_grand_automata.util.StorageProvider
import io.github.fate_grand_automata.util.messageAndStackTrace
import io.github.fate_grand_automata.util.set
import io.github.fate_grand_automata.util.showOverlayDialog
import io.github.lib_automata.EntryPoint
import io.github.lib_automata.ScreenshotService
import io.github.lib_automata.ScriptAbortException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.time.Duration.Companion.milliseconds

@ServiceScoped
class ScriptManager @Inject constructor(
    private val service: Service,
    @ApplicationContext private val context: Context,
    private val imageLoader: ImageLoader,
    private val preferences: IPreferences,
    private val prefsCore: PrefsCore,
    private val storageProvider: StorageProvider,
    private val messages: ScriptMessages,
    private val uiStateHolder: ScriptRunnerUIStateHolder,
    private val clipboardManager: ClipboardManager,
    private val messageBox: ScriptRunnerMessageBox,
    @ServiceCoroutineScope private val scope: CoroutineScope,
    private val launcherResponseHandler: ScriptLauncherResponseHandler
) {
    var scriptState: ScriptState = ScriptState.Stopped
        private set

    private suspend fun showBattleExit(
        context: Context,
        exception: AutoBattle.ExitException
    ) = withContext(Dispatchers.Main) {
        suspendCancellableCoroutine<Unit> { continuation ->
            var dialog: DialogInterface? = null

            val composeView = FakedComposeView(context) {
                BattleExit(
                    exception = exception,
                    prefs = preferences,
                    prefsCore = prefsCore,
                    onClose = { dialog?.dismiss() },
                    onCopy = {
                        clipboardManager.set(context, exception)
                    }
                )
            }

            dialog = showOverlayDialog(context) {
                setView(composeView.view)

                setOnDismissListener {
                    composeView.close()
                    continuation.resume(Unit)
                }
            }
        }
    }

    // TODO remove suppression when AutoGiftBox exit message is localized
    @SuppressLint("StringFormatMatches")
    private fun onScriptExit(e: Exception) = scope.launch {
        uiStateHolder.uiState = ScriptRunnerUIState.Idle
        uiStateHolder.isPlayButtonEnabled = false
        imageLoader.clearSupportCache()

        // Stop recording
        scriptState.let { state ->
            if (state is ScriptState.Started) {
                scriptState = ScriptState.Stopping(state)
            }

            val recording = when (state) {
                ScriptState.Stopped -> null
                is ScriptState.Started -> state.recording
                is ScriptState.Stopping -> state.start.recording
            }

            if (recording != null) {
                // A little bit of delay so the exit message can be recorded
                launch {
                    try {
                        delay(500)
                        withContext(Dispatchers.Main) {
                            recording.close()
                        }
                    } catch (e: Exception) {
                        val msg = context.getString(R.string.cannot_stop_recording)
                        Timber.e(e, msg)
                        withContext(Dispatchers.Main) {
                            Toast.makeText(service, "${msg}: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }

                    uiStateHolder.isRecording = false
                }
            }
        }

        val scriptExitedString by lazy { context.getString(R.string.script_exited) }

        when (e) {
            is SupportImageMaker.ExitException -> {
                when (e.reason) {
                    SupportImageMaker.ExitReason.NotFound -> {
                        val msg = context.getString(R.string.support_img_maker_not_found)

                        messages.notify(msg)
                        messageBox.show(scriptExitedString, msg)
                    }

                    SupportImageMaker.ExitReason.Success -> showSupportImageNamer(context, storageProvider)
                }
            }

            is AutoLottery.ExitException -> {
                val msg = when (e.reason) {
                    AutoLottery.ExitReason.PresentBoxFull -> context.getString(R.string.present_box_full)
                    AutoLottery.ExitReason.RanOutOfCurrency -> context.getString(R.string.lottery_currency_depleted)
                }

                messages.notify(msg)
                messageBox.show(scriptExitedString, msg)
            }

            is AutoGiftBox.ExitException -> {
                val msg = when (val reason = e.reason) {
                    is AutoGiftBox.ExitReason.CannotSelectAnyMore -> context.getString(
                        R.string.picked_exp_stacks,
                        reason.pickedStacks,
                        reason.pickedGoldEmbers
                    )

                    AutoGiftBox.ExitReason.NoEmbersFound -> context.getString(R.string.no_embers_found)
                }

                messages.notify(msg)
                messageBox.show(scriptExitedString, msg)
            }

            is AutoFriendGacha.ExitException -> {
                val msg = when (val reason = e.reason) {
                    AutoFriendGacha.ExitReason.InventoryFull -> context.getString(R.string.inventory_full)
                    is AutoFriendGacha.ExitReason.Limit -> context.getString(R.string.times_rolled, reason.count)
                }

                messages.notify(msg)
                messageBox.show(scriptExitedString, msg)
            }

            is AutoBattle.ExitException -> {
                preferences.hidePlayButton = false

                if (e.reason !is AutoBattle.ExitReason.Abort) {
                    messages.notify(scriptExitedString)
                }

                showBattleExit(service, e)
            }
            is AutoServantLevel.ExitException -> {
                val msg = when (val reason = e.reason) {
                    AutoServantLevel.ExitReason.NoServantSelected ->
                        context.getString(R.string.servant_enhancement_none_selected)

                    is AutoServantLevel.ExitReason.Unexpected -> {
                        e.let {
                            "${context.getString(R.string.unexpected_error)}: ${reason.exception.message}"
                        }
                    }

                    AutoServantLevel.ExitReason.MaxLevelAchieved ->
                        context.getString(R.string.servant_enhancement_max_level)

                    AutoServantLevel.ExitReason.NoEmbersOrQPLeft ->
                        context.getString(R.string.servant_enhancement_no_embers_or_qp_left)

                    AutoServantLevel.ExitReason.Abort ->
                        context.getString(R.string.servant_enhancement_aborted)

                    AutoServantLevel.ExitReason.RedirectAscension ->
                        context.getString(R.string.servant_enhancement_redirect_ascension_success)
                    AutoServantLevel.ExitReason.RedirectGrail ->
                        context.getString(R.string.servant_enhancement_redirect_grail_success)

                    AutoServantLevel.ExitReason.UnableToPerformAscension ->
                        context.getString(R.string.servant_enhancement_perform_ascension_failed)
                }
                messageBox.show(scriptExitedString, msg)
            }

            is Metaspace.ExitException -> {
                when (e.reason) {
                    is Metaspace.ExitReason.InvalidTeam -> {
                        val msg = "Your team appears invalid for this battle. Please update your team and try again."
                        messages.notify(msg)
                        messageBox.show(scriptExitedString, msg)
                    }
                    else -> {
                        messages.notify("Metaspace completed: ${e.state.runsCompleted} runs")
                    }
                }
            }

            is Map.ExitException -> {
                val isCompleted = e.reason == Map.ExitReason.Completed
                val message = "Map ${if (isCompleted) "Complete" else "Failed!"}: ${e.state.boxesCollected} boxes, ${e.state.battlesFought} battles.${if (e.state.offerAvailable) " You have an Offer to check" else ""}"
                if (!isCompleted) {
                    when (e.reason) {
                        is Map.ExitReason.Completed -> null
                        is Map.ExitReason.Failure -> Timber.w("Map exited with Failure")
                        is Map.ExitReason.Unexpected -> Timber.e(e.reason.cause, "Map exited with Unexpected: ${e.reason.cause?.message}")
                    }
                }
                messages.notify(message)
                messageBox.show(scriptExitedString, message)
            }

            is VoidMirror.ExitException -> {
                when (e.reason) {
                    is VoidMirror.ExitReason.MaxRetriesReached -> {
                        val msg = "Defeat limit reached after ${e.state.totalAttempts} attempts with ${e.state.successfulRuns} wins"
                        messages.notify(msg)
                        messageBox.show(scriptExitedString, msg)
                    }
                    else -> {
                        val msg = "Void Mirror completed: ${e.state.successfulRuns} successful, ${e.state.totalAttempts} attempts, ${e.state.failedRetries} failed retries"
                        messages.notify(msg)
                    }
                }
            }

            is Dailies.ExitException -> {
                messages.notify("Dailies completed: ${e.state.stepsCompleted} steps")
            }

            is ScriptAbortException -> {
                // user aborted. do nothing
            }

            is AutoCEBomb.ExitException -> {
                val msg = when (e.reason) {
                    AutoCEBomb.ExitReason.NoSuitableTargetCEFound -> "No suitable target CE found"
                }

                messages.notify(msg)
                messageBox.show(scriptExitedString, msg)
            }

            is KnownException -> {
                messages.notify(scriptExitedString)

                messageBox.show(scriptExitedString, e.reason.msg)
            }

            else -> {
                println(e.messageAndStackTrace)

                val msg = context.getString(R.string.unexpected_error)
                messages.notify(msg)

                messageBox.show(msg, e.messageAndStackTrace, e)
            }
        }

        scriptState = ScriptState.Stopped
        delay(250.milliseconds)
        uiStateHolder.isPlayButtonEnabled = true
    }

    private fun getEntryPoint(entryPoint: ScriptEntryPoint): EntryPoint =
        when (preferences.scriptMode) {
            ScriptModeEnum.Battle -> entryPoint.battle()
            ScriptModeEnum.FP -> entryPoint.fp()
            ScriptModeEnum.Lottery -> entryPoint.lottery()
            ScriptModeEnum.PresentBox -> entryPoint.giftBox()
            ScriptModeEnum.SupportImageMaker -> entryPoint.supportImageMaker()
            ScriptModeEnum.CEBomb -> entryPoint.ceBomb()
            ScriptModeEnum.ServantLevel -> entryPoint.servantLevel()
        }

    enum class PauseAction {
        Pause, Resume, Toggle
    }

    fun pause(action: PauseAction): Boolean {
        scriptState.let { state ->
            if (state is ScriptState.Started) {
                if (state.paused && action != PauseAction.Pause) {
                    uiStateHolder.uiState = ScriptRunnerUIState.Running
                    state.entryPoint.exitManager.resume()

                    state.paused = false

                    return true
                } else if (!state.paused && action != PauseAction.Resume) {
                    state.entryPoint.exitManager.pause()
                    uiStateHolder.uiState = ScriptRunnerUIState.Paused(state.entryPoint.pausedStatus())

                    state.paused = true

                    return true
                }
            }
        }

        return false
    }

    private fun updateGameServer() {
        val server = prefsCore.gameServerRaw.get()

        preferences.gameServer =
            if (server == PrefsCore.GAME_SERVER_AUTO_DETECT)
                (TapperService.instance?.detectedFgoServer ?: GameServer.default).also {
                    Timber.d("Using auto-detected Game Server: $it")
                }
            else try {
                GameServer.deserialize(server)?.also {
                    Timber.d("Using Game Server: $it")
                } ?: GameServer.default
            } catch (e: Exception) {
                Timber.e(e, "Game Server: Falling back to NA")

                GameServer.default
            }
    }

    fun startScript(
        context: Context,
        screenshotService: ScreenshotService,
        componentBuilder: ScriptComponentBuilder
    ) {
        updateGameServer()

        if (scriptState !is ScriptState.Stopped) {
            return
        }

        uiStateHolder.isPlayButtonEnabled = false

        val scriptComponent = componentBuilder
            .screenshotService(screenshotService)
            .build()

        val hiltEntryPoint = EntryPoints.get(scriptComponent, ScriptEntryPoint::class.java)

        scope.launch {
            val bnaChoice = bnaScriptPicker(context)

            uiStateHolder.isPlayButtonEnabled = true

            if (bnaChoice != null) {
                delay(500)
                runEntryPoint(
                    screenshotService = screenshotService,
                    entryPointProvider = {
                        when (bnaChoice) {
                            BnaScript.Metaspace -> hiltEntryPoint.metaspace()
                            BnaScript.Map -> hiltEntryPoint.map()
                            BnaScript.VoidMirror -> hiltEntryPoint.voidMirror()
//                            BnaScript.Dailies -> hiltEntryPoint.dailies()
                        }
                    }
                )
            }
        }
    }

    fun stopScript() {
        scriptState.let { state ->
            if (state is ScriptState.Started) {
                uiStateHolder.isPlayButtonEnabled = false
                scriptState = ScriptState.Stopping(state)
                state.entryPoint.stop()
            }
        }
    }

    private suspend fun runEntryPoint(screenshotService: ScreenshotService, entryPointProvider: () -> EntryPoint) {
        if (scriptState !is ScriptState.Stopped) {
            return
        }

        val recording = try {
            if (preferences.recordScreen) {
                withContext(Dispatchers.Main) {
                    screenshotService.startRecording()
                }
            } else null
        } catch (e: Exception) {
            val msg = context.getString(R.string.cannot_start_recording)
            Timber.e(e, msg)
            withContext(Dispatchers.Main) {
                Toast.makeText(service, "${msg}: ${e.message}", Toast.LENGTH_SHORT).show()
            }

            null
        }

        val entryPoint = entryPointProvider().apply {
            scriptExitListener = { onScriptExit(it) }
        }

        scriptState = ScriptState.Started(entryPoint, recording)
        uiStateHolder.uiState = ScriptRunnerUIState.Running

        if (recording != null) {
            uiStateHolder.isRecording = true
        }

        entryPoint.run()
    }

    private suspend fun scriptPicker(
        context: Context,
        detectedMode: ScriptModeEnum
    ) = withContext(Dispatchers.Main) {
        suspendCoroutine<ScriptLauncherResponse> { continuation ->

            var dialog: DialogInterface? = null

            val composeView = FakedComposeView(context) {
                ScriptLauncher(
                    scriptMode = detectedMode,
                    onResponse = {
                        continuation.resume(it)
                        dialog?.dismiss()
                    },
                    prefs = preferences,
                    prefsCore = prefsCore
                )
            }

            dialog = showOverlayDialog(context) {
                setView(composeView.view)

                setOnDismissListener {
                    uiStateHolder.isPlayButtonEnabled = true
                    composeView.close()
                    try {
                        continuation.resume(ScriptLauncherResponse.Cancel)
                    } catch (e: IllegalStateException) {
                        // Ignore exception on resuming twice
                    }
                }
            }
        }
    }

    fun showStatus(status: Exception) {
        if (status is AutoBattle.ExitException) {
            scope.launch { showBattleExit(service, status) }
        }
    }

    private suspend fun bnaScriptPicker(
        context: Context
    ): BnaScript? = withContext(Dispatchers.Main) {
        suspendCoroutine { continuation ->
            var dialog: DialogInterface? = null

            val composeView = FakedComposeView(context) {
                BnaScriptLauncher(
                    onResponse = {
                        continuation.resume(it)
                        dialog?.dismiss()
                    }
                )
            }

            dialog = showOverlayDialog(context) {
                setView(composeView.view)

                setOnDismissListener {
                    composeView.close()
                    try {
                        continuation.resume(null)
                    } catch (_: IllegalStateException) {
                    }
                }
            }
        }
    }
}