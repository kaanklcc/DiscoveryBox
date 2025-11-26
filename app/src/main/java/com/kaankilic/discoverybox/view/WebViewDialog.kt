package com.kaankilic.discoverybox.view

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun WebViewDialog(
    title: String,
    htmlContent: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                title,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            AndroidView(
                factory = { context ->
                    WebView(context).apply {
                        webViewClient = WebViewClient()
                        settings.javaScriptEnabled = false
                        loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(500.dp)
            )
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Kapat")
            }
        }
    )
}

fun getPrivacyPolicyHtml(): String {
    return """
<!DOCTYPE html>
<html lang="tr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <style>
        body { font-family: Arial, sans-serif; padding: 16px; line-height: 1.6; }
        h2 { color: #003366; margin-top: 20px; }
        ul { padding-left: 20px; }
    </style>
</head>
<body>
    <h2>1. Toplanan Bilgiler</h2>
    <ul>
        <li>Google hesap bilgileri (email, ad)</li>
        <li>Kullanıcı tarafından oluşturulan hikayeler</li>
        <li>Cihaz bilgileri (deneme sistemi için)</li>
    </ul>
    
    <h2>2. Bilgilerin Kullanımı</h2>
    <ul>
        <li>Hikaye oluşturma hizmetlerinin sağlanması</li>
        <li>Kullanıcı hesaplarının yönetimi</li>
        <li>Ücretsiz deneme sisteminin kötüye kullanımının önlenmesi</li>
    </ul>
    
    <h2>3. Veri Depolama</h2>
    <ul>
        <li>Hikayeler Firebase Firestore'da saklanır</li>
        <li>Görseller Firebase Storage'da saklanır</li>
        <li>Verilerinizi üçüncü taraflarla paylaşmıyoruz</li>
        <li>Reklam sistemi kullanmıyoruz</li>
    </ul>
    
    <h2>4. Çocukların Gizliliği</h2>
    <p>Uygulamamız çocuklar için tasarlanmıştır. COPPA düzenlemelerine uyuyoruz.</p>
    
    <h2>5. İletişim</h2>
    <p>E-posta: kan.klc.1903@gmail.com</p>
</body>
</html>
    """.trimIndent()
}

fun getTermsOfUseHtml(): String {
    return """
<!DOCTYPE html>
<html lang="tr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <style>
        body { font-family: Arial, sans-serif; padding: 16px; line-height: 1.6; }
        h2 { color: #003366; margin-top: 20px; }
        ul { padding-left: 20px; }
    </style>
</head>
<body>
    <h2>1. Hizmet Tanımı</h2>
    <p>Discovery Box, yapay zeka destekli çocuk hikayeleri oluşturma platformudur.</p>
    
    <h2>2. Deneme Sistemi</h2>
    <ul>
        <li>Cihaz başına bir ücretsiz deneme</li>
        <li>Premium abonelik sınırsız erişim sağlar</li>
    </ul>
    
    <h2>3. Kullanıcı İçeriği</h2>
    <ul>
        <li>Oluşturduğunuz hikayelerin sahipliği size aittir</li>
        <li>Anonim verileri hizmetleri geliştirmek için kullanabiliriz</li>
    </ul>
    
    <h2>4. Yasak Kullanımlar</h2>
    <ul>
        <li>Ücretsiz denemeyi kötüye kullanmak için birden fazla hesap oluşturmak</li>
        <li>Uygulamayı yasadışı amaçlarla kullanmak</li>
    </ul>
    
    <h2>5. İletişim</h2>
    <p>E-posta: kan.klc.1903@gmail.com</p>
</body>
</html>
    """.trimIndent()
}
