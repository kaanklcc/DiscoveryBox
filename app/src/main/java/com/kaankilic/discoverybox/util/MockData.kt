package com.kaankilic.discoverybox.util

object MockData {
    
    fun getMockStory(prompt: String): String {
        return """
Bir zamanlar büyülü bir ormanda yaşayan küçük bir tavşan vardı.
---SAYFA---
Tavşan her gün yeni arkadaşlar edinmek için ormanı geziyordu.
---SAYFA---
Bir gün sincap, kurbağa ve kuş ile tanıştı ve hepsi en iyi arkadaş oldular.
        """.trimIndent()
    }
    
    fun getMockLongStory(prompt: String): String {
        return """
Bir zamanlar büyülü bir ormanda yaşayan küçük bir tavşan vardı. Tavşan çok meraklı ve cesur bir hayvandı.
---SAYFA---
Tavşan her gün yeni arkadaşlar edinmek için ormanı geziyordu. Ormanda birçok ilginç yer keşfetti.
---SAYFA---
Bir gün sincap, kurbağa ve kuş ile tanıştı. Onlarla birlikte harika maceralar yaşadı.
---SAYFA---
Sonunda hepsi en iyi arkadaş oldular ve mutlu bir şekilde ormanda yaşamaya devam ettiler.
        """.trimIndent()
    }
    
    fun getMockShortStory(prompt: String): String {
        return """
Bir zamanlar küçük bir tavşan vardı.
---SAYFA---
Tavşan yeni arkadaşlar buldu ve mutlu oldu.
        """.trimIndent()
    }
}
