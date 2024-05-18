package rltut.screens

import asciiPanel.AsciiPanel
import rltut.{World, WorldBuilder}

import java.awt.event.KeyEvent

trait Screen {
  def displayOutput(terminal: AsciiPanel): Unit
  def respondToUserInput(key: KeyEvent): Screen
}

class StartScreen extends Screen {
  override def displayOutput(terminal: AsciiPanel): Unit = {
    terminal.write("rl tutorial", 1, 1)
    terminal.writeCenter("-- press [enter] to start --", 22)
  }

  override def respondToUserInput(key: KeyEvent): Screen = {
    key.getKeyCode match {
      case KeyEvent.VK_ENTER =>
        new PlayScreen
      case _ =>
        this
    }
  }
}

class PlayScreen(screenWidth: Int = 80, screenHeight: Int = 21) extends Screen {
  private var centerX: Int = 0
  private var centerY: Int = 0

  private val world: World = new WorldBuilder(90, 31).makeCaves().build()

  private def getScrollX: Int =
    Math.max(0, Math.min(centerX - screenWidth / 2, world.width - screenWidth))

  private def getScrollY: Int =
    Math.max(0, Math.min(centerY - screenHeight / 2, world.height - screenHeight))

  private def displayTiles(terminal: AsciiPanel, left: Int, top: Int): Unit = {
    for (x <- 0 until screenWidth) {
      for (y <- 0 until screenHeight) {
        val wx = x + left
        val wy = y + top
        terminal.write(world.glyph(wx, wy), x, y, world.color(wx, wy))
      }
    }
  }

  private def scrollBy(mx: Int, my: Int): Unit = {
    centerX = Math.max(0, Math.min(centerX + mx, world.width - 1))
    centerY = Math.max(0, Math.min(centerY + my, world.height - 1))
  }

  override def displayOutput(terminal: AsciiPanel): Unit = {
    terminal.write("You are having so much fun.", 1, 1)
    terminal.writeCenter("-- press [escape] to lose or [enter] to win --", 22)

    val left = getScrollX
    val top = getScrollY
    displayTiles(terminal, left, top)
    terminal.write('X', centerX - left, centerY - top)
  }

  override def respondToUserInput(key: KeyEvent): Screen = {
    key.getKeyCode match {
      case KeyEvent.VK_LEFT | KeyEvent.VK_H =>
        scrollBy(-1, 0)
        this
      case KeyEvent.VK_RIGHT | KeyEvent.VK_L =>
        scrollBy(1, 0)
        this
      case KeyEvent.VK_UP | KeyEvent.VK_K =>
        scrollBy(0, -1)
        this
      case KeyEvent.VK_DOWN | KeyEvent.VK_J =>
        scrollBy(0, 1)
        this
      case KeyEvent.VK_Y =>
        scrollBy(-1, -1)
        this
      case KeyEvent.VK_U =>
        scrollBy(1, -1)
        this
      case KeyEvent.VK_B =>
        scrollBy(-1, 1)
        this
      case KeyEvent.VK_N =>
        scrollBy(1, 1)
        this
      case KeyEvent.VK_ESCAPE =>
        new LoseScreen
      case KeyEvent.VK_ENTER =>
        new WinScreen
      case _ =>
        this
    }
  }
}

class WinScreen extends Screen {
  override def displayOutput(terminal: AsciiPanel): Unit = {
    terminal.write("You won!", 1, 1)
    terminal.writeCenter("-- press [enter] to restart --", 22)
  }

  override def respondToUserInput(key: KeyEvent): Screen = {
    key.getKeyCode match {
      case KeyEvent.VK_ENTER =>
        new PlayScreen
      case _ =>
        this
    }
  }
}

class LoseScreen extends Screen {
  override def displayOutput(terminal: AsciiPanel): Unit = {
    terminal.write("You lost :(", 1, 1)
    terminal.writeCenter("-- press [enter] to restart --", 22)
  }

  override def respondToUserInput(key: KeyEvent): Screen = {
    key.getKeyCode match {
      case KeyEvent.VK_ENTER =>
        new PlayScreen
      case _ =>
        this
    }
  }
}
