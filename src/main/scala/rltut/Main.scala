package rltut

import asciiPanel.AsciiPanel
import rltut.screens.{ Screen, StartScreen }

import java.awt.event.{ KeyEvent, KeyListener }
import javax.swing.JFrame

/**
  * See http://trystans.blogspot.com/2011/08/roguelike-tutorial-02-input-output.html
  */
object Main {
  def main(args: Array[String]): Unit = {
    val app = new Application()
    app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
    app.setLocation(10, 10)
    app.setVisible(true)
  }
}

class Application() extends JFrame with KeyListener {
  private val terminal = new AsciiPanel
  private var screen: Screen = new StartScreen

  add(terminal)
  pack()
  addKeyListener(this)
  repaint()

  override def repaint(): Unit = {
    terminal.clear()
    screen.displayOutput(terminal)
    super.repaint()
  }

  override def keyPressed(e: KeyEvent): Unit = {
    screen = screen.respondToUserInput(e)
    repaint()
  }

  override def keyReleased(e: KeyEvent): Unit = {}

  override def keyTyped(e: KeyEvent): Unit = {}
}
