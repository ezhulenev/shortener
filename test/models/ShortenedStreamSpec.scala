package models

import org.specs2.mutable._

class ShortenedStreamSpec extends Specification {

  "ShortenedStream" should {
    "create valid shortened sequence from one length string" in {
      val stream = ShortenedStream("a")
      val first4 = stream.take(5).toList
      first4 must equalTo("a" :: "aa" :: "aaa" :: "aaaa" :: "aaaaa" :: Nil)
    }

    "create valid shortened sequence from '0' and '1'" in {
      val stream = ShortenedStream("01")
      val first4 = stream.take(15).toList
      first4 must equalTo("0" :: "1" :: "00" :: "01" :: "10" :: "11" :: "000" :: "001" :: "010" :: "011" :: "100" :: "101" :: "110" :: "111" :: "0000" :: Nil)
    }
  }
}