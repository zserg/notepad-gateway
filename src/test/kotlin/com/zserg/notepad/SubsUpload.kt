//package com.zserg.flashcards
//
//import com.zserg.flashcards.model.*
//import com.zserg.flashcards.srtparser.SRTParser
//import com.zserg.flashcards.utils.SrtUtils
//import org.junit.jupiter.api.Test
//import org.springframework.http.HttpEntity
//import org.springframework.web.client.RestTemplate
//
//
//class SubsUpload {
//
//    @Test
//    fun upload() {
//        val list: ArrayList<PairSubs> = arrayListOf()
//
//        list.add(
//            getPairSubs(
//                "subtitles/1x02 Purple Giraffe.srt",
//                "subtitles/How.I.Met.Your.Mother.S01E02.Purple Giraffe.srt",
//                "How I met your mother (S01E02)",
//                3,
//                456,
//                1,
//                402
//            )
//        )
//
//        list.add(
//            getPairSubs(
//                "subtitles/SiliconValley/1x1_Minimum_Viable_Product_(Pilot).srt",
//                "subtitles/SiliconValley/Silicon.Valley.S01E01.HDTV.x264-KILLERS.srt",
//                "Silicon Valley (S01E01)",
//                1, 591, 2, 603
//            )
//        )
//
//        list.add(
//            getPairSubs(
//                "subtitles/friends/Friends.S02E01.The.One.with.Rosss.New.Girlfriend.RU.srt",
//                "subtitles/friends/friends.s02e01.720p.bluray.x264-psychd.srt",
//                "Friends (S02E01)",
//                2, 340, 1, 351
//            )
//        )
//
//        list.add(
//            getPairSubs(
//                "subtitles/suits/suits.s01e02.hdtv.xvid.srt",
//                "subtitles/suits/Suits.S01E02.HDTV.XviD-FQM.srt",
//                title = "Suits (S02E01)",
//                1, 606, 1, 902
//            )
//        )
//
//        list.add(
//            getPairSubs(
//                "subtitles/2_Broke_Girl/2.Broke.Girls.S01E02.HDTV.720p.Sub.srt",
//                "subtitles/2_Broke_Girl/2 Broke Girls S01E02 And the Break-Up Scene.DVDRip.NonHI.en.WB.srt",
//                "2 Broke girl (S01E02)",
//                1, 471, 1, 374
//            )
//        )
//
//        list.forEach {
//            val restTemplate = RestTemplate()
//            val request: HttpEntity<PairSubs> = HttpEntity<PairSubs>(it)
//            restTemplate.postForObject("http://s4.zserg.net:8089/pairsubs", request, String::class.java)
//        }
//
//
//    }
//
//    private fun getPairSubs(
//        fileRu: String, fileEn: String, title: String, start1: Long, end1: Long, start2: Long, end2: Long
//    ): PairSubs {
//        val subs_ru = read(fileRu, Language.RU)
//        val subs_en = read(fileEn, Language.EN)
//        val pairSubs = PairSubs(
//            title = title,
//            subs1 = Subs(Language.RU, subs_ru),
//            subs2 = Subs(Language.EN, subs_en),
//            config = PairSubsConfig(start1, end1, start2, end2)
//        )
//        return pairSubs
//    }
//
//    private fun read(file: String, language: Language): List<SubItem> {
//        val subs = SRTParser.getSubtitlesFromFile(file)
//        val items = subs?.map { SrtUtils.subtitleToSubItem(it) } ?: emptyList()
//        return items
//    }
//
//}