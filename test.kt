import okhttp3.HttpUrl.Companion.toHttpUrl

fun main() {
    val reqUrl = "http://10.0.2.2:3002/api/portfolio".toHttpUrl()
    val customUrl = "http://56.228.3.36:3001"
    val uri = java.net.URI(customUrl)
    
    val newUrl = reqUrl.newBuilder()
        .scheme(uri.scheme)
        .host(uri.host)
        .build()
        
    println(newUrl.toString())
}
