package com.kaankilic.discoverybox.util

import android.util.Log
import com.google.ai.client.generativeai.type.GenerateContentResponse

/**
 * Gemini API maliyet takip sistemi
 * 
 * Fiyatlandırma (Ocak 2025):
 * - Gemini 2.0 Flash Lite (Metin): ₺0.0125 / 1000 token
 * - Gemini 2.5 Flash Image (Görsel): ₺1.25 / 1000 token
 */
object ApiCostTracker {
    
    // Gemini API fiyatları (TL cinsinden, 1000 token başına)
    private const val GEMINI_TEXT_PRICE_PER_1K = 0.0125 // ₺0.0125 / 1000 token
    private const val GEMINI_IMAGE_PRICE_PER_1K = 1.25  // ₺1.25 / 1000 token
    
    data class CostInfo(
        val inputTokens: Int,
        val outputTokens: Int,
        val totalTokens: Int,
        val costTL: Double,
        val apiType: String // "TEXT" veya "IMAGE"
    )
    
    /**
     * Metin API maliyetini hesapla
     */
    fun calculateTextCost(response: GenerateContentResponse): CostInfo {
        val metadata = response.usageMetadata
        val inputTokens = metadata?.promptTokenCount ?: 0
        val outputTokens = metadata?.candidatesTokenCount ?: 0
        val totalTokens = metadata?.totalTokenCount ?: (inputTokens + outputTokens)
        
        val costTL = (totalTokens / 1000.0) * GEMINI_TEXT_PRICE_PER_1K
        
        val info = CostInfo(
            inputTokens = inputTokens,
            outputTokens = outputTokens,
            totalTokens = totalTokens,
            costTL = costTL,
            apiType = "TEXT"
        )
        
        logCost(info)
        return info
    }
    
    /**
     * Görsel API maliyetini hesapla
     * Not: Gemini Image API response'unda usageMetadata olmayabilir,
     * bu durumda manuel token sayısı verilmeli
     */
    fun calculateImageCost(tokenCount: Int): CostInfo {
        val costTL = (tokenCount / 1000.0) * GEMINI_IMAGE_PRICE_PER_1K
        
        val info = CostInfo(
            inputTokens = 0,
            outputTokens = tokenCount,
            totalTokens = tokenCount,
            costTL = costTL,
            apiType = "IMAGE"
        )
        
        logCost(info)
        return info
    }
    
    /**
     * Görsel API maliyetini response'dan hesapla (eğer metadata varsa)
     */
    fun calculateImageCostFromResponse(response: com.google.gson.JsonObject): CostInfo {
        // Gemini Image API response'undan token sayısını çıkar
        val usageMetadata = response.getAsJsonObject("usageMetadata")
        val totalTokens = usageMetadata?.get("totalTokenCount")?.asInt ?: 14217 // Ortalama değer
        
        return calculateImageCost(totalTokens)
    }
    
    private fun logCost(info: CostInfo) {
        Log.i("💰 API_COST", """
            ═══════════════════════════════════
            API Tipi: ${info.apiType}
            Input Token: ${info.inputTokens}
            Output Token: ${info.outputTokens}
            Toplam Token: ${info.totalTokens}
            Maliyet: ₺${String.format("%.4f", info.costTL)}
            ═══════════════════════════════════
        """.trimIndent())
    }
    
    /**
     * Hikaye maliyetini hesapla (metin + görseller)
     */
    fun calculateStoryCost(
        textCost: CostInfo,
        imageCosts: List<CostInfo>
    ): StoryCostSummary {
        val totalImageCost = imageCosts.sumOf { it.costTL }
        val totalCost = textCost.costTL + totalImageCost
        
        val summary = StoryCostSummary(
            textCost = textCost,
            imageCosts = imageCosts,
            totalTextTokens = textCost.totalTokens,
            totalImageTokens = imageCosts.sumOf { it.totalTokens },
            totalCostTL = totalCost
        )
        
        logStoryCost(summary)
        return summary
    }
    
    data class StoryCostSummary(
        val textCost: CostInfo,
        val imageCosts: List<CostInfo>,
        val totalTextTokens: Int,
        val totalImageTokens: Int,
        val totalCostTL: Double
    )
    
    private fun logStoryCost(summary: StoryCostSummary) {
        Log.i("💰 STORY_COST", """
            ╔═══════════════════════════════════════╗
            ║       HİKAYE MALİYET RAPORU          ║
            ╠═══════════════════════════════════════╣
            ║ Metin Token: ${summary.totalTextTokens.toString().padEnd(23)}║
            ║ Metin Maliyet: ₺${String.format("%.4f", summary.textCost.costTL).padEnd(20)}║
            ║                                       ║
            ║ Görsel Sayısı: ${summary.imageCosts.size.toString().padEnd(22)}║
            ║ Görsel Token: ${summary.totalImageTokens.toString().padEnd(22)}║
            ║ Görsel Maliyet: ₺${String.format("%.4f", summary.imageCosts.sumOf { it.costTL }).padEnd(19)}║
            ║                                       ║
            ║ TOPLAM MALİYET: ₺${String.format("%.4f", summary.totalCostTL).padEnd(18)}║
            ╚═══════════════════════════════════════╝
        """.trimIndent())
    }
}
