package rltut

import asciiPanel.AsciiPanel

import java.awt.Color
import scala.collection.mutable

class Creature(
  val world: World,
  val glyph: Char,
  val color: Color,
  val maxHp: Int,
  val attackValue: Int,
  val defenseValue: Int
) {
  var x: Int = 0
  var y: Int = 0
  var hp: Int = maxHp

  private var ai: Option[CreatureAi] = None
  def setCreatureAi(ai: CreatureAi): Unit = this.ai = Some(ai)

  def dig(wx: Int, wy: Int): Unit = {
    doAction("dig")
    world.dig(wx, wy)
  }
  def moveBy(mx: Int, my: Int): Unit = {
    world
      .creature(x + mx, y + my)
      .map(attack)
      .getOrElse(ai.foreach(_.onEnter(x + mx, y + my, world.tile(x + mx, y + my))))
  }

  def canEnter(wx: Int, wy: Int): Boolean = world.tile(wx, wy).isGround && world.creature(wx, wy).isEmpty

  private def attack(creature: Creature): Unit = {
    var amount: Int = Math.max(0, attackValue - creature.defenseValue)

    amount = (Math.random * amount).toInt + 1

    doAction(s"attack the '${creature.glyph}' for $amount damage.")
    creature.modifyHp(-amount)
  }

  private def modifyHp(amount: Int): Unit = {
    doAction(s"take ${Math.abs(amount)} damage")
    hp += amount
    if (hp < 1) {
      doAction("die")
      world.remove(this)
    }
  }

  def update(): Unit = ai.foreach(_.onUpdate())
  def notify(message: String): Unit = ai.foreach(_.notify(message))

  def doAction(message: String): Unit = {
    val range = 9
    val rangeSquared = range * range

    for (ox <- -range until range + 1) {
      for (oy <- -range until range + 1) {
        if (ox * ox <= rangeSquared) {
          world.creature(x + ox, y + oy).foreach { other =>
            if (other == this) {
              other.notify(s"You $message.")
            } else {
              other.notify(s"The '$glyph' ${message.makeSecondPerson()}")
            }
          }
        }
      }
    }
  }
}

trait CreatureAi {
  protected val creature: Creature
  def onEnter(x: Int, y: Int, tile: Tile): Unit = {}
  def onUpdate(): Unit = {}
  def notify(message: String): Unit = {}
}

case class PlayerAi(override protected val creature: Creature, messages: mutable.ListBuffer[String])
    extends CreatureAi {

  creature.setCreatureAi(this)

  override def onEnter(x: Int, y: Int, tile: Tile): Unit = {
    if (tile.isGround) {
      creature.x = x
      creature.y = y
    } else if (tile.isDiggable) {
      creature.dig(x, y)
    }
  }

  override def notify(message: String): Unit = messages += message
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
      creature.doAction("spawn a child")
    }
  }
}

class CreatureFactory(private val world: World) {
  def newPlayer(messages: mutable.ListBuffer[String]): Creature = {
    val player = new Creature(world, '@', AsciiPanel.brightWhite, maxHp = 100, attackValue = 20, defenseValue = 25)
    world.addAtEmptyLocation(player)
    PlayerAi(player, messages)
    player
  }

  def newFungus: Creature = {
    val fungus = new Creature(world, 'f', AsciiPanel.green, maxHp = 10, attackValue = 0, defenseValue = 0)
    world.addAtEmptyLocation(fungus)
    FungusAi(fungus, this)
    fungus
  }
}
