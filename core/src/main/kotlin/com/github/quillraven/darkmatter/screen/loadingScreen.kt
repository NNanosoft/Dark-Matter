package com.github.quillraven.darkmatter.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.github.quillraven.darkmatter.Game
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import ktx.app.KtxScreen
import ktx.assets.async.AssetStorage
import ktx.async.KtxAsync
import ktx.log.logger
import kotlin.system.measureTimeMillis

private val LOG = logger<LoadingScreen>()

class LoadingScreen(
    private val game: Game,
    private val batch: Batch = game.batch,
    private val assets: AssetStorage = game.assets
) : KtxScreen {
    override fun show() {
        LOG.debug { "Show" }

        val timeToLoadAndInit = measureTimeMillis {
            val assetRefs = listOf(
                assets.loadAsync<TextureAtlas>("graphics/graphics.atlas")
            )
            KtxAsync.launch {
                assetRefs.joinAll()
                assetsLoaded()
            }
        }
        LOG.debug { "It took ${timeToLoadAndInit * 0.001f} seconds to load assets and initialize" }
    }

    private fun assetsLoaded() {
        game.addScreen(GameScreen(game))
    }

    override fun render(delta: Float) {
        if (assets.progress.isFinished && Gdx.input.justTouched()) {
            game.removeScreen(LoadingScreen::class.java)
            dispose()
            game.setScreen<GameScreen>()
        }
    }

    override fun dispose() {
        LOG.debug { "Dispose" }
    }
}
