package rltut.screens

import asciiPanel.AsciiPanel

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

class PlayScreen extends Screen {
  override def displayOutput(terminal: AsciiPanel): Unit = {
    terminal.write("You are having so much fun.", 1, 1)
    terminal.writeCenter("-- press [escape] to lose or [enter] to win --", 22)
  }

  override def respondToUserInput(key: KeyEvent): Screen = {
    key.getKeyCode match {
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
