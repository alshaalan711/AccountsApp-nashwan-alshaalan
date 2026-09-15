package com.example.accountsapp

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.*
import org.json.JSONArray
import org.json.JSONObject

data class Entry(
    val date: String,
    val period: String,
    val network: Double,
    val cash: Double
) {
    fun total() = network + cash
}

class MainActivity : ComponentActivity() {
    private val store by lazy { AccountStore(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { AccountsApp(store) }
    }
}

class AccountStore(private val context: Context) {
    private val prefs = context.getSharedPreferences("accounts", Context.MODE_PRIVATE)
    var entries by mutableStateOf(load())
        private set

    private fun load(): List<Entry> {
        val raw = prefs.getString("entries", "[]") ?: "[]"
        val a = JSONArray(raw)
        return (0 until a.length()).map {
            val o = a.getJSONObject(it)
            Entry(o.getString("date"), o.getString("period"), o.getDouble("network"), o.getDouble("cash"))
        }
    }

    fun add(e: Entry) {
        entries = entries + e
        save()
    }

    fun clear() { entries = emptyList(); save() }

    private fun save() {
        val a = JSONArray()
        entries.forEach {
            a.put(JSONObject().apply {
                put("date", it.date); put("period", it.period)
                put("network", it.network); put("cash", it.cash)
            })
        }
        prefs.edit().putString("entries", a.toString()).apply()
    }

    fun backupJson(): String {
        val a = JSONArray()
        entries.forEach {
            a.put(JSONObject().apply {
                put("date", it.date); put("period", it.period)
                put("network", it.network); put("cash", it.cash)
            })
        }
        return a.toString()
    }
}

@Composable
fun AccountsApp(store: AccountStore) {
    var tab by remember { mutableIntStateOf(0) }
    var month by remember { mutableIntStateOf(Calendar.getInstance().get(Calendar.MONTH)) }
    val months = listOf("يناير","فبراير","مارس","أبريل","مايو","يونيو","يوليو","أغسطس","سبتمبر","أكتوبر","نوفمبر","ديسمبر")
    val monthEntries = store.entries.filter {
        runCatching { SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(it.date) }.getOrNull()?.let {
            Calendar.getInstance().apply { time = it }.get(Calendar.MONTH) == month
        } ?: false
    }

    MaterialTheme {
        CompositionLocalProvider(LocalLayoutDirection provides androidx.compose.ui.unit.LayoutDirection.Rtl) {
            Scaffold(
                topBar = {
                    TopAppBar(title = { Text("نظام الحسابات") })
                },
                bottomBar = {
                    NavigationBar {
                        NavigationBarItem(tab == 0, { tab = 0 }, label = { Text("الحسابات") }, icon = {})
                        NavigationBarItem(tab == 1, { tab = 1 }, label = { Text("التقارير") }, icon = {})
                        NavigationBarItem(tab == 2, { tab = 2 }, label = { Text("النسخ الاحتياطي") }, icon = {})
                    }
                }
            ) { pad ->
                when (tab) {
                    0 -> AccountsScreen(store, month, { month = it }, months, monthEntries, Modifier.padding(pad))
                    1 -> ReportsScreen(store, months, Modifier.padding(pad))
                    else -> BackupScreen(store, Modifier.padding(pad))
                }
            }
        }
    }
}

@Composable
fun AccountsScreen(
    store: AccountStore, month: Int, onMonth: (Int)->Unit, months: List<String>,
    entries: List<Entry>, modifier: Modifier = Modifier
) {
    var network by remember { mutableStateOf("") }
    var cash by remember { mutableStateOf("") }
    var period by remember { mutableStateOf("نهار") }
    val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
    val total = entries.sumOf { it.total() }

    LazyColumn(modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Text("الشهر: ${months[month]}", style = MaterialTheme.typography.headlineSmall)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button({ onMonth((month + 11) % 12) }) { Text("السابق") }
                Button({ onMonth((month + 1) % 12) }) { Text("التالي") }
            }
        }
        item {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("إضافة مبيعات — $today", style = MaterialTheme.typography.titleMedium)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(period == "نهار", { period = "نهار" }, { Text("نهار") })
                        FilterChip(period == "ليل", { period = "ليل" }, { Text("ليل") })
                    }
                    OutlinedTextField(network, { network = it }, label={Text("شبكة")}, keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Decimal), modifier=Modifier.fillMaxWidth())
                    OutlinedTextField(cash, { cash = it }, label={Text("كاش")}, keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Decimal), modifier=Modifier.fillMaxWidth())
                    Button({
                        store.add(Entry(today, period, network.toDoubleOrNull() ?: 0.0, cash.toDoubleOrNull() ?: 0.0))
                        network = ""; cash = ""
                    }, Modifier.fillMaxWidth()) { Text("حفظ العملية") }
                }
            }
        }
        item {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("إجمالي الشهر", style=MaterialTheme.typography.titleMedium)
                    Text(String.format(Locale.US, "%.2f", total), style=MaterialTheme.typography.headlineMedium)
                }
            }
        }
        item { Text("الحركات", style=MaterialTheme.typography.titleLarge) }
        items(entries.sortedByDescending { it.date + it.period }) { e ->
            ListItem(
                headlineContent = { Text("${e.date} — ${e.period}") },
                supportingContent = { Text("شبكة: ${e.network} | كاش: ${e.cash} | الإجمالي: ${e.total()}") }
            )
        }
    }
}

@Composable
fun ReportsScreen(store: AccountStore, months: List<String>, modifier: Modifier) {
    val yearTotal = store.entries.sumOf { it.total() }
    LazyColumn(modifier.fillMaxSize().padding(16.dp), verticalArrangement=Arrangement.spacedBy(8.dp)) {
        item { Text("التقرير السنوي", style=MaterialTheme.typography.headlineSmall) }
        item { Text("الإجمالي السنوي: ${String.format(Locale.US, "%.2f", yearTotal)}", style=MaterialTheme.typography.titleLarge) }
        months.forEachIndexed { i, m ->
            val t = store.entries.filter {
                runCatching { SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(it.date) }.getOrNull()?.let {
                    Calendar.getInstance().apply { time = it }.get(Calendar.MONTH) == i
                } ?: false
            }.sumOf { it.total() }
            item { ListItem(headlineContent={Text(m)}, supportingContent={Text(String.format(Locale.US, "%.2f", t))}) }
        }
    }
}

@Composable
fun BackupScreen(store: AccountStore, modifier: Modifier) {
    var msg by remember { mutableStateOf("") }
    val context = LocalContext.current
    Column(modifier.fillMaxSize().padding(16.dp), verticalArrangement=Arrangement.spacedBy(12.dp)) {
        Text("النسخ الاحتياطي", style=MaterialTheme.typography.headlineSmall)
        Text("بياناتك محفوظة محليًا. يمكن تجهيز ربط Google Drive/حساب Google للنسخ التلقائي في نسخة الإنتاج.")
        Button({
            val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                type = "application/json"
                putExtra(android.content.Intent.EXTRA_TEXT, store.backupJson())
            }
            context.startActivity(android.content.Intent.createChooser(intent, "حفظ النسخة الاحتياطية"))
            msg = "تم تجهيز ملف النسخة الاحتياطية للمشاركة أو الحفظ."
        }, Modifier.fillMaxWidth()) { Text("إنشاء نسخة احتياطية") }
        if (msg.isNotEmpty()) Text(msg)
    }
}

