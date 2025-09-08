import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import com.example.compose.AppTheme
import org.jetbrains.compose.resources.ExperimentalResourceApi
import ui.homescreen.HomeScreen

@OptIn(ExperimentalResourceApi::class)
@Composable
fun App() {
    AppTheme {
        Navigator(HomeScreen)
    }
}
