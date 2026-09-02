package com.yilmazbilgin.cagannevabakicitakip

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

private const val DAILY_FEE = 2000

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            BakiciTakipTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFFFFF8F5)
                ) {
                    BakiciTakipApp(applicationContext)
                }
            }
        }
    }
}

@androidx.compose.runtime.Composable
fun BakiciTakipTheme(
    content: @androidx.compose.runtime.Composable () -> Unit
) {
    MaterialTheme(
        content = content
    )
}

@androidx.compose.runtime.Composable
fun BakiciTakipApp(context: Context) {

    val today = remember { LocalDate.now() }

    var selectedMonth by remember {
        mutableStateOf(YearMonth.from(today))
    }

    var savedDays by remember {
        mutableStateOf(loadSavedDays(context))
    }
        var paidDays by remember {
        mutableStateOf(loadPaidDays(context))
        }

    val monthKey = selectedMonth.toString()

    val selectedDays = savedDays[monthKey] ?: emptySet()

    val totalFee = selectedDays.size * DAILY_FEE

val paidDayCount = selectedDays.count { day ->
    paidDays[monthKey]?.contains(day) == true
}

val paidFee = paidDayCount * DAILY_FEE

val remainingFee = totalFee - paidFee

    val monthName = selectedMonth.month
        .getDisplayName(TextStyle.FULL, Locale("tr", "TR"))
        .replaceFirstChar { it.uppercase(Locale("tr", "TR")) }

    val daysInMonth = selectedMonth.lengthOfMonth()

    val firstDay = selectedMonth.atDay(1)

    // Pazartesi = 1 ... Pazar = 7
    val firstDayOffset = firstDay.dayOfWeek.value - 1

    val calendarItems = buildList<Int?> {
        repeat(firstDayOffset) {
            add(null)
        }

        for (day in 1..daysInMonth) {
            add(day)
        }
    }

    fun changeMonth(offset: Long) {
        selectedMonth = selectedMonth.plusMonths(offset)
    }

    Column(
        modifier = Modifier
    .fillMaxSize()
.statusBarsPadding()
.background(Color(0xFFFFF8F5))
    .padding(horizontal = 16.dp)
    .padding(top = 12.dp, bottom = 12.dp)
    ) {

        // ÜST BAŞLIK
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "🧸 Çağan & Neva",
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF55415A)
                )

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = "Bakıcı Takibi",
                    fontSize = 14.sp,
                    color = Color(0xFF907D91)
                )
            }

            Box(
                modifier = Modifier
                    .size(46.dp)
                    .background(
                        color = Color(0xFFFFDDE5),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "💗",
                    fontSize = 22.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // AY DEĞİŞTİRME
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                TextButton(
                    onClick = {
                        changeMonth(-1)
                    }
                ) {
                    Text(
                        text = "‹",
                        fontSize = 32.sp,
                        color = Color(0xFF806080)
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = monthName,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF55415A)
                    )

                    Text(
                        text = selectedMonth.year.toString(),
                        fontSize = 13.sp,
                        color = Color(0xFF9B899B)
                    )
                }

                TextButton(
                    onClick = {
                        changeMonth(1)
                    }
                ) {
                    Text(
                        text = "›",
                        fontSize = 32.sp,
                        color = Color(0xFF806080)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ÖZET KARTLARI
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            SummaryCard(
                modifier = Modifier.weight(1f),
                emoji = "👶",
                title = "Bakıcıda",
                value = "${selectedDays.size} gün",
                background = Color(0xFFE8F7F1)
            )

            SummaryCard(
                modifier = Modifier.weight(1f),
                emoji = "💰",
                title = "Toplam",
                value = "${formatMoney(totalFee)} TL",
                background = Color(0xFFFFE8D8)
            )
        }
            Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {

        SummaryCard(
            modifier = Modifier.weight(1f),
            emoji = "✅",
            title = "Ödenen",
            value = "${formatMoney(paidFee)} TL",
            background = Color(0xFFE2F5E9)
        )

        SummaryCard(
            modifier = Modifier.weight(1f),
            emoji = "⏳",
            title = "Kalan",
            value = "${formatMoney(remainingFee)} TL",
            background = Color(0xFFFFF0D9)
        )
            }

        Spacer(modifier = Modifier.height(15.dp))

        // TAKVİM
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {

            Column(
                modifier = Modifier.padding(12.dp)
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    val weekdays = listOf(
                        "Pzt",
                        "Sal",
                        "Çar",
                        "Per",
                        "Cum",
                        "Cmt",
                        "Paz"
                    )

                    weekdays.forEach { dayName ->

                        Text(
                            text = dayName,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF9A899A)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(7),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(315.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(7.dp)
                ) {

                    items(calendarItems) { day ->

                        if (day == null) {

                            Spacer(
                                modifier = Modifier.size(40.dp)
                            )

                        } else {

                            val isSelected = selectedDays.contains(day)
val isPaid = paidDays[monthKey]?.contains(day) == true
                            val isToday =
                                selectedMonth == YearMonth.from(today) &&
                                day == today.dayOfMonth

                            CalendarDay(
    day = day,
    selected = isSelected,
    paid = isPaid,
    today = isToday,
    onClick = {

    val currentDays =
        (savedDays[monthKey] ?: emptySet()).toMutableSet()

    val currentPaidDays =
        (paidDays[monthKey] ?: emptySet()).toMutableSet()

    if (!currentDays.contains(day)) {

        // 1. dokunuş: Bakıcı günü
        currentDays.add(day)

    } else if (!currentPaidDays.contains(day)) {

        // 2. dokunuş: Ödendi
        currentPaidDays.add(day)

    } else {

        // 3. dokunuş: Günü tamamen kaldır
        currentDays.remove(day)
        currentPaidDays.remove(day)
    }

    savedDays =
        savedDays.toMutableMap().apply {
            this[monthKey] = currentDays
        }

    paidDays =
        paidDays.toMutableMap().apply {
            this[monthKey] = currentPaidDays
        }

    saveDays(
        context = context,
        data = savedDays
    )

    savePaidDays(
        context = context,
        data = paidDays
    )
    }
                                    )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ALT BİLGİ
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFF0E8FA)
            )
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "💡",
                    fontSize = 22.sp
                )

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = "Günlük bakıcı ücreti",
                        fontSize = 12.sp,
                        color = Color(0xFF8B7894)
                    )

                    Text(
                        text = "2.000 TL",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF5C4964)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Column(
                    horizontalAlignment = Alignment.End
                ) {

                    Text(
                        text = "Bu ay",
                        fontSize = 12.sp,
                        color = Color(0xFF8B7894)
                    )

                    Text(
                        text = "${selectedDays.size} × 2.000 TL",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF5C4964)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(7.dp))

        Text(
            text = "Takvime dokunarak bakıcıya verilen günleri işaretleyin.",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontSize = 11.sp,
            color = Color(0xFF9D8D9D)
        )
    }
}

@androidx.compose.runtime.Composable
fun SummaryCard(
    modifier: Modifier = Modifier,
    emoji: String,
    title: String,
    value: String,
    background: Color
) {

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = background
        )
    ) {

        Column(
            modifier = Modifier.padding(14.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = emoji,
                    fontSize = 19.sp
                )

                Spacer(modifier = Modifier.width(7.dp))

                Text(
                    text = title,
                    fontSize = 12.sp,
                    color = Color(0xFF7D6D7D)
                )
            }

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF55415A)
            )
        }
    }
}

@androidx.compose.runtime.Composable
fun CalendarDay(
    day: Int,
selected: Boolean,
paid: Boolean,
today: Boolean,
onClick: () -> Unit
) {

    val backgroundColor =
    if (paid) {
        Color(0xFFD8F3DC)
    } else if (selected) {
        Color(0xFFFFD3DE)
    } else {
        Color(0xFFFFFBFA)
    }
    val textColor =
        if (selected) {
            Color(0xFF8C4660)
        } else {
            Color(0xFF5E5360)
        }

    Box(
        modifier = Modifier
            .size(40.dp)
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(13.dp)
            )
            .clickable {
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = day.toString(),
                fontSize = 14.sp,
                fontWeight = if (selected || today) {
                    FontWeight.Bold
                } else {
                    FontWeight.Normal
                },
                color = textColor
            )

            if (today) {

                Box(
                    modifier = Modifier
                        .padding(top = 2.dp)
                        .size(4.dp)
                        .background(
                            color = Color(0xFFB889C8),
                            shape = CircleShape
                        )
                )
            }
        }
    }
}

fun formatMoney(value: Int): String {
    return "%,d".format(Locale("tr", "TR"), value)
}

fun loadSavedDays(context: Context): Map<String, Set<Int>> {

    val preferences = context.getSharedPreferences(
        "bakici_takip",
        Context.MODE_PRIVATE
    )

    val result = mutableMapOf<String, Set<Int>>()

    preferences.all.forEach { (month, value) ->

        val text = value as? String ?: return@forEach

        val days = text
            .split(",")
            .filter { it.isNotBlank() }
            .mapNotNull { it.toIntOrNull() }
            .toSet()

        result[month] = days
    }

    return result
}

fun saveDays(
    context: Context,
    data: Map<String, Set<Int>>
) {

    val preferences = context.getSharedPreferences(
        "bakici_takip",
        Context.MODE_PRIVATE
    )

    preferences.edit().clear().apply {

        data.forEach { (month, days) ->

            putString(
                month,
                days.sorted().joinToString(",")
            )
        }

        apply()
    }
}
fun loadPaidDays(context: Context): Map<String, Set<Int>> {
    val preferences = context.getSharedPreferences(
        "bakici_takip_paid",
        Context.MODE_PRIVATE
    )

    val result = mutableMapOf<String, Set<Int>>()

    preferences.all.forEach { (month, value) ->
        val text = value as? String ?: return@forEach

        val days = text
            .split(",")
            .filter { it.isNotBlank() }
            .mapNotNull { it.toIntOrNull() }
            .toSet()

        result[month] = days
    }

    return result
}

fun savePaidDays(
    context: Context,
    data: Map<String, Set<Int>>
) {
    val preferences = context.getSharedPreferences(
        "bakici_takip_paid",
        Context.MODE_PRIVATE
    )

    preferences.edit().clear().apply {
        data.forEach { (month, days) ->
            putString(
                month,
                days.sorted().joinToString(",")
            )
        }

        apply()
    }
}
