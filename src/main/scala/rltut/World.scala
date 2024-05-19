package rltut

import asciiPanel.AsciiPanel

import java.awt.Color
import scala.annotation.unused

case class Tile(glyph: Char, color: Color) extends Enumeration {
  def isDiggable: Boolean = this == Tile.WALL
  def isGround: Boolean = this != Tile.WALL && this != Tile.BOUNDS

  override def toString = s"Tile(${glyph.toInt}, $color)"
}

@unused // IJ isn't smart enough to figure this out...
object Tile extends Enumeration {
  val FLOOR: Tile = Tile(250.toChar, AsciiPanel.yellow)
  val WALL: Tile = Tile(177.toChar, AsciiPanel.yellow)
  val BOUNDS: Tile = Tile('x', AsciiPanel.brightRed)
}

class World(tiles: Array[Array[Tile]]) {

  val width: Int = tiles.length
  val height: Int = tiles(0).length

  def tile(x: Int, y: Int): Tile = {
    if (x < 0 || x >= width || y < 0 || y >= height) {
      Tile.BOUNDS
    } else {
      tiles(x)(y)
    }
  }

  def glyph(x: Int, y: Int): Char = tile(x, y).glyph
  def color(x: Int, y: Int): Color = tile(x, y).color

  def dig(x: Int, y: Int): Unit = {
    if (tiles(x)(y).isDiggable) {
      tiles(x)(y) = Tile.FLOOR
    }
  }

  def addAtEmptyLocation(creature: Creature): Unit = {
    var x: Int = 0
    var y: Int = 0

    do {
      x = (Math.random * width).toInt
      y = (Math.random * height).toInt
    } while (!tiles(x)(y).isGround)

    creature.x = x
    creature.y = y
  }
}

class WorldBuilder(width: Int, height: Int) {
  private var tiles = Array.ofDim[Tile](width, height)

  def makeCaves(): WorldBuilder = randomizeTiles().smooth(8)

  private def randomizeTiles(): WorldBuilder = {
    for (x <- 0 until width) {
      for (y <- 0 until height) {
        tiles(x)(y) = if (Math.random() < 0.5) Tile.FLOOR else Tile.WALL
      }
    }
    this
  }

  private def smooth(times: Int): WorldBuilder = {
    val tiles2 = Array.ofDim[Tile](width, height)

    for (_ <- 0 until times) {

      for (x <- 0 until width) {
        for (y <- 0 until height) {
          var floors = 0
          var rocks = 0

          for (ox <- -1 until 2) {
            for (oy <- -1 until 2) {
              if (!(x + ox < 0 || x + ox >= width || y + oy < 0) && !(y + oy >= height)) {
                if (tiles(x + ox)(y + oy) == Tile.FLOOR) {
                  floors += 1
                } else {
                  rocks += 1
                }
              }
            }
          }
          tiles2(x)(y) = if (floors >= rocks) Tile.FLOOR else Tile.WALL
        }
      }
      tiles = tiles2
    }
    this
  }

  def build(): World = new World(tiles)
}
