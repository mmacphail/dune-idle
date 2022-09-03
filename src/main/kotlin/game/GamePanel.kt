package game

import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.awt.image.BufferedImage
import javax.swing.JPanel

class GamePanel : JPanel(), Runnable, KeyListener, MouseListener {
    companion object GamePanel {
        val WIDTH = 800
        val HEIGHT = 800
        val FPS = 30L
    }

    var averageFps: Double = 0.0

    lateinit var thread: Thread
    var running = false

    lateinit var image: BufferedImage
    lateinit var g: Graphics2D

    init {
        preferredSize = Dimension(width, height)
        isFocusable = true
        requestFocus()
    }

    override fun addNotify() {
        super.addNotify()
        thread = Thread(this)
        thread.start()
        addKeyListener(this)
        addMouseListener(this)
    }

    override fun run() {
        running = true

        image = BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB)
        g = image.graphics as Graphics2D

        Init.init()

        var startTime = 0L
        var urdTimeMillis = 0L
        var waitTime = 0L
        var totalTime = 0L
        var frameTime = 0.0

        var frameCount = 0
        val maxFrameCount = 30

        val targetTime = 1000 / FPS

        while (running) {
            startTime = System.nanoTime()

            gameUpdate(frameTime)
            gameRender()
            gameDraw()

            urdTimeMillis = (System.nanoTime() - startTime) / 1_000_000

            waitTime = targetTime - urdTimeMillis

            try {
                Thread.sleep(waitTime)
            } catch (ex: Exception) {
            }

            totalTime += System.nanoTime() - startTime
            frameTime = (System.nanoTime() - startTime).toDouble() / 1_000_000_000
            frameCount++

            if (frameCount == maxFrameCount) {
                averageFps = 1000.0 / ((totalTime / frameCount) / 1_000_000)
                frameCount = 0
                totalTime = 0
            }
        }
    }

    private fun gameUpdate(dt: Double) {
        Game.updateEntities(dt)
    }

    private fun gameRender() {
        with(g) {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            color = Color(245, 191, 66)
            fillRect(0, 0, WIDTH, HEIGHT)
            color = Color.BLACK
            drawString("FPS: $averageFps", 10, 10)
        }

        Game.drawEntities(g)
    }

    private fun gameDraw() {
        val g2 = this.graphics
        g2.drawImage(image, 0, 0, null)
        g2.dispose()
    }

    override fun keyTyped(e: KeyEvent) {
    }

    override fun keyPressed(e: KeyEvent) {
        Inputs.keyPressed(e)
    }


    override fun keyReleased(e: KeyEvent) {
        Inputs.keyReleased(e)
    }

    override fun mouseClicked(e: MouseEvent?) {

    }

    override fun mousePressed(e: MouseEvent) {
        Inputs.mousePressed(e)
    }

    override fun mouseReleased(e: MouseEvent?) {

    }

    override fun mouseEntered(e: MouseEvent?) {

    }

    override fun mouseExited(e: MouseEvent?) {
    }
}