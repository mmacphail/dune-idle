package game

import java.awt.Dimension
import javax.swing.JFrame

fun main(args: Array<String>) {
    val window = JFrame("Idle Dune")
    window.defaultCloseOperation = JFrame.EXIT_ON_CLOSE

    window.contentPane = GamePanel()

    window.preferredSize = Dimension(800, 800)
    window.pack()
    window.isVisible = true
}

