package rltut

import asciiPanel.AsciiPanel

import java.awt.Color

class Creature(val world: World, val glyph: Char, val color: Color) {
  var x: Int = 0
  var y: Int = 0

  private var ai: Option[CreatureAi] = None
  def setCreatureAi(ai: CreatureAi): Unit = this.ai = Some(ai)

  def dig(wx: Int, wy: Int): Unit = world.dig(wx, wy)
  def moveBy(mx: Int, my: Int): Unit = ai.foreach(_.onEnter(x + mx, y + my, world.tile(x + mx, y + my)))
}

trait CreatureAi {
  protected val creature: Creature
  def onEnter(x: Int, y: Int, tile: Tile): Unit
}

class PlayerAi(_creature: Creature) extends CreatureAi {
  protected val creature: Creature = _creature

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

class CreatureFactory(private val world: World) {
  def newPlayer: Creature = {
    val player = new Creature(world, '@', AsciiPanel.brightWhite)
    world.addAtEmptyLocation(player)
    new PlayerAi(player)
    player
  }
}
