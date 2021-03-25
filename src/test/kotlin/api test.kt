import com.wynnlab.items.WynnItem
import com.wynnlab.items.getAPIResults

fun main() {
    val items = getAPIResults("Crusade")
    print(items)

    //val wynnItem = WynnItem.parse(items[0])
    //print(wynnItem)
}