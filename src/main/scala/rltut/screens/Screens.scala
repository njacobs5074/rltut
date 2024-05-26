package rltut.screens

import asciiPanel.AsciiPanel
import rltut.{ Creature, CreatureFactory, World, WorldBuilder }

import java.awt.event.KeyEvent
import scala.collection.mutable

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

  private val world: World = new WorldBuilder(90, 31).makeCaves().build()
  private val messages: mutable.ListBuffer[String] = mutable.ListBuffer.empty

  private val creatureFactory = new CreatureFactory(world)
  private val player: Creature = creatureFactory.newPlayer(messages)
  for (_ <- 0 until 8) {
    creatureFactory.newFungus
  }

  private def getScrollX: Int =
    Math.max(0, Math.min(player.x - screenWidth / 2, world.width - screenWidth))

  private def getScrollY: Int =
    Math.max(0, Math.min(player.y - screenHeight / 2, world.height - screenHeight))

  private def displayTiles(terminal: AsciiPanel, left: Int, top: Int): Unit = {
    for (x <- 0 until screenWidth) {
      for (y <- 0 until screenHeight) {
        val wx = x + left
        val wy = y + top

        world
          .creature(wx, wy)
          .map(creature => terminal.write(creature.glyph, creature.x - left, creature.y - top, creature.color))
          .getOrElse(terminal.write(world.glyph(wx, wy), x, y, world.color(wx, wy)))
      }
    }
  }

  private def displayMessages(terminal: AsciiPanel): Unit = {
    val top = screenHeight - messages.size
    messages.zipWithIndex.foreach { case (message, i) =>
      terminal.writeCenter(message, top + i)
    }
    messages.clear()
  }

  override def displayOutput(terminal: AsciiPanel): Unit = {
    terminal.write("You are having so much fun.", 1, 1)
    terminal.writeCenter("-- press [escape] to lose or [enter] to win --", 22)

    // Display the world and the player
    val left = getScrollX
    val top = getScrollY
    displayTiles(terminal, left, top)
    terminal.write(player.glyph, player.x - left, player.y - top, player.color)

    // Write status info and any notifications
    terminal.write(f" ${player.hp}%3d/${player.maxHp}%3d hp", 1, 23)
    displayMessages(terminal)
  }

  override def respondToUserInput(key: KeyEvent): Screen = {
    key.getKeyCode match {
      case KeyEvent.VK_LEFT | KeyEvent.VK_H =>
        player.moveBy(-1, 0)
        world.update()
        this
      case KeyEvent.VK_RIGHT | KeyEvent.VK_L =>
        player.moveBy(1, 0)
        world.update()
        this
      case KeyEvent.VK_UP | KeyEvent.VK_K =>
        player.moveBy(0, -1)
        world.update()
        this
      case KeyEvent.VK_DOWN | KeyEvent.VK_J =>
        player.moveBy(0, 1)
        world.update()
        this
      case KeyEvent.VK_Y =>
        player.moveBy(-1, -1)
        world.update()
        this
      case KeyEvent.VK_U =>
        player.moveBy(1, -1)
        world.update()
        this
      case KeyEvent.VK_B =>
        player.moveBy(-1, 1)
        world.update()
        this
      case KeyEvent.VK_N =>
        player.moveBy(1, 1)
        world.update()
        this
      case KeyEvent.VK_SPACE =>
        player.doAction("rest")
        world.update()
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
