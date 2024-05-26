package object rltut {

  implicit class StringExt(s: String) {

    // Naively assume that the first token is a verb
    def makeSecondPerson(): String = {
      val tokens = s.split(" ")
      tokens.head + "s " + tokens.tail.mkString(" ")
    }
  }

}
