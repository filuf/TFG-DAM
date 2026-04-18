# Retrofit en Android
Guía práctica para usar Retrofit en Android.
# 1. Configurar entorno
## 1.1 Configurar dependias
#### En ``build.gradle.kts`` a nivel de aplicación:
```kotlin
android {
    ...
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    ...
    implementation("com.squareup.retrofit2:retrofit:3.0.0")
    implementation("com.squareup.retrofit2:converter-gson:3.0.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.10.0")
}
```
Añade las dependencias de retrofit, viewModels y activa el binding.

## 1.2 Modificar ``AndroidManifest.xml`` para poder realizar peticiones.
Ubicado en ``app/src/main/AndroidManifest.xml``
```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">
    <uses-permission android:name="android.permission.INTERNET"/> # permite conectarse a internet
    <application
        android:usesCleartextTraffic="true"> # permite realizar peticiones http
        ...
    </application>
</manifest>
```

---

# 2. Componentes y conceptos básicos
## 1. Clases
Las clases empleadas son las siguientes:
- ``Response`` respuesta de la petición.
- ``SongResponse`` modelo propio a emplear.
- ``Retrofit`` objeto para crear la instancia que conectará con nuestra API REST.
## 2. Anotaciones
### 2.1 Modelo de datos
- ``@SerializedName(<nombre en json>)``
para guardar el campo del json con un nombre personalizado en tu clase.
```kotlin
data class SongResponse (
    val id: String,
    val codigo:String,
    @SerializedName("titulo") // Valor original del campo en el JSON de mis objetos canción en mi api rest
    val título: String, // Valor custom
)
```
### 2.2 Interfaz ``APIService.kt``
Interfaz que será empleada como servicio de nuestra aplicación.
- ``@GET, @POST, @PATCH, @DELETE...`` métodos http, se especifican encima de las funciones del método.
- ``@Url`` especifica el endpoint. Normalmente, se especifica una url base cuando se instancia el servicio mediante la clase Retrofit (se vé mas adelante). Se pasa como parámetro del método.
- ``@Path`` especifica parámetros. También se para como parámetro del método.
- ``@Body`` especifica el cuerpo de la petición. **En métodos GET no se pueden mandar**. También se para como parámetro del método.

Ejemplo:
```kotlin
@GET("cancion/{codigo}")
suspend fun getSongByID(@Path("codigo") codigo: String): Response<SongResponse>

@POST
suspend fun postSong(@Body song: SongResponse, @Url url:String): Response<SongResponse>

@PATCH("cancion/update")
suspend fun updateCancion(@Body song: SongResponse): Response<SongResponse>

@DELETE("cancion/{codigo}")
suspend fun deleteCancionByCodigo(@Path("codigo") codigo: String): Response<SongResponse>
```

### 2.3 ViewModel y conexión con el servicio
#### a) Crea una instancia de retrofit especificando la ``url base``, la que estará presente en todas tus peticiones.
```kotlin
private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8080") // localhost en android
        .addConverterFactory(GsonConverterFactory.create())
        .build()
```
#### b) Crea una instancia del servicio usando el patrón **Singleton**.
```kotlin
private val service = retrofit.create(APIService::class.java)
```
Ejemplo práctico de un fragmento del ViewModel en el que se hace un GET manejando errores en la petición:
```kotlin
private val _songs = MutableLiveData<List<SongResponse?>>()
val songs: LiveData<List<SongResponse?>> = _songs

fun getSongs() {
    viewModelScope.launch {
        val response = withContext(Dispatchers.IO) {
            service.getSongs()
        }
        if (response.isSuccessful) {
            _songs.postValue(response.body())
        }else{
            Log.i("Error en la carga","Petición fallida. ${response.code()}")
            _songs.postValue(mutableListOf(null))
        }
    }
}
```

---

# Código de ejemplo por si quereis probarlo vosotros mismos
El mío está comentado por si nos os fiáis de Gemini :)

## 1. Definir el Modelo de Datos
Crea las clases que representen la estructura del JSON. En la API de Rick & Morty, los personajes vienen dentro de una lista llamada `results`:
#### Clase ``Character.kt``:
```kotlin
data class Character(
    val id: Int, 
    val name: String, 
    val status: String, 
    val species: String, 
    val image: String 
)
```
#### Clase ``Character.kt``:
```kotlin
data class CharacterResponse( val results: List<Character> )
```


## 2. Crear la Interfaz de API (Service)
Define los "endpoints" (puntos de acceso) de la API mediante una interfaz de Kotlin utilizando anotaciones de Retrofit
```kotlin
import retrofit2.Response 
import retrofit2.http.GET

interface ApiService { 
    @GET("character") 
    suspend fun getAllCharacters(): Response<CharacterResponse> 
}
```

## 3. Crear el Objeto Retrofit (Cliente)
Utilizamos el patrón **Singleton** para asegurarnos de tener una única instancia de Retrofit en toda la aplicación
```kotlin
import retrofit2.Retrofit 
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient { 
    private const val BASE_URL = "https://rickandmortyapi.com/api/"
    val instance: ApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(ApiService::class.java)
    }
}
```


## 4. Implementar el ViewModel
El ViewModel gestiona la llamada a la API en un hilo secundario (Corrutina) y expone los datos a la vista mediante LiveData.
```kotlin
import androidx.lifecycle.* 
import kotlinx.coroutines.launch

class CharacterViewModel : ViewModel() {
    private val _characterList = MutableLiveData<List<Character>>()
    val characterList: LiveData<List<Character>> get() = _characterList
    
    fun fetchCharacters() {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.getAllCharacters()
                if (response.isSuccessful) {
                    // Actualizamos el LiveData con la lista recibida
                    _characterList.value = response.body()?.results
                }
            } catch (e: Exception) {
                // Manejo de errores (ej. falta de conexión)
            }
        }
    }
}
```


## 5. Mostrar los datos en la Activity (ViewBinding)
Finalmente, conectamos todo en la Activity. Observamos el LiveData y actualizamos la interfaz de usuario cuando los datos cambian.
```kotlin
import android.os.Bundle 
import androidx.activity.viewModels 
import androidx.appcompat.app.AppCompatActivity 
import com.example.tuapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: CharacterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)
    
        // Observar los datos del ViewModel
        viewModel.characterList.observe(this) { characters ->
            if (!characters.isNullOrEmpty()) {
                // Ejemplo: Mostrar el nombre del primer personaje en un TextView
                binding.tvNombre.text = "Personaje: ${characters[0].name}"
                binding.tvEstado.text = "Estado: ${characters[0].status}"
            }
        }
    
        // Realizar la petición
        viewModel.fetchCharacters()
    }
}
```
