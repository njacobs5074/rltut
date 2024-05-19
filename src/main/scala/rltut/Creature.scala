package rltut

import asciiPanel.AsciiPanel

import java.awt.Color

class Creature(val world: World, val glyph: Char, val color: Color) {
  var x: Int = 0
  var y: Int = 0

  private var ai: Option[CreatureAi] = None
  def setCreatureAi(ai: CreatureAi): Unit = this.ai = Some(ai)

  def dig(wx: Int, wy: Int): Unit = world.dig(wx, wy)
  def moveBy(mx: Int, my: Int): Unit = {
    world
      .creature(x + mx, y + my)
      .map(attack)
      .getOrElse(ai.foreach(_.onEnter(x + mx, y + my, world.tile(x + mx, y + my))))
  }

  def canEnter(wx: Int, wy: Int): Boolean = world.tile(wx, wy).isGround && world.creature(wx, wy).isEmpty

  private def attack(creature: Creature): Unit = {
    world.remove(creature)
  }

  def update(): Unit = ai.foreach(_.onUpdate())
}

trait CreatureAi {
  protected val creature: Creature
  def onEnter(x: Int, y: Int, tile: Tile): Unit = {}
  def onUpdate(): Unit = {}
}

case class PlayerAi(override protected val creature: Creature) extends CreatureAi {

  creature.setCreatureAi(this)

  override def onEnter(x: Int, y: Int, tile: Tile): Unit = {
    if (tile.isGround) {
      creature.x = x
      creature.y = y
    } else if (tile.isDiggable) {
      creature.dig(x, y)
    }
  }
}

case class FungusAi(override val creature: Creature, creatureFactory: CreatureFactory) extends CreatureAi {
  private var spreadCount: Int = 0
  creature.setCreatureAi(this)

  override def onUpdate(): Unit = {
    if (spreadCount < 5 && Math.random() < 0.02) {
      spread()
    }
  }

  private def spread(): Unit = {
    val x = creature.x + (Math.random() * 11).toInt - 5
    val y = creature.y + (Math.random() * 11).toInt - 5

    if (creature.canEnter(x, y)) {
      val child = creatureFactory.newFungus
      child.x = x
      child.y = y
      spreadCount += 1
    }
  }
}

class CreatureFactory(private val world: World) {
  def newPlayer: Creature = {
    val player = new Creature(world, '@', AsciiPanel.brightWhite)
    world.addAtEmptyLocation(player)
    PlayerAi(player)
    player
  }

  def newFungus: Creature = {
    val fungus = new Creature(world, 'f', AsciiPanel.green)
    world.addAtEmptyLocation(fungus)
    FungusAi(fungus, this)
    fungus
  }
}
