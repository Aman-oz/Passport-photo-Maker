package com.ots.aipassportphotomaker.domain.model.language

import android.app.Activity
import android.content.Context
import com.ots.aipassportphotomaker.R

/**
 * @author Aman Ullah
 * Sr. Software Engineer
 */


data class Language(
    val id: Int,
    val name: String,
    val code: String,
    val image: Int,
    val nativeName:String,
    val buttonText:String,
    val titleText: String = "Select Language",
    val defaultText: String = "Default",
    val otherLanguages: String = "Other Languages"

)


fun getLanguageList(context: Context): List<Language> {
    val languageList: MutableList<Language> = ArrayList()
    languageList.add(
        Language(
            id = 1,
            name = context.resources.getString(R.string.english),
            code ="en",
            image = R.drawable.flag_english,
            nativeName = context.resources.getString(R.string.english_native),
            buttonText = context.resources.getString(R.string.done_english),
            titleText = context.resources.getString(R.string.select_language_en),
            defaultText = context.resources.getString(R.string.default_language_en),
            otherLanguages = context.resources.getString(R.string.other_languages_en)
        )
    )

    languageList.add(
        Language(
            id = 2,
            name = context.resources.getString(R.string.spanish),
            code ="es",
            image = R.drawable.flag_spanish,
            nativeName = context.resources.getString(R.string.spanish_native),
            buttonText = context.resources.getString(R.string.done_spanish),
            titleText = context.resources.getString(R.string.select_language_es),
            defaultText = context.resources.getString(R.string.default_language_es),
            otherLanguages = context.resources.getString(R.string.other_languages_es)
        )
    )

    languageList.add(
        Language(
            id = 3,
            name = context.resources.getString(R.string.hindi),
            code ="hi",
            image = R.drawable.flag_hindi,
            nativeName = context.resources.getString(R.string.hindi_native),
            buttonText = context.resources.getString(R.string.done_hindi),
            titleText = context.resources.getString(R.string.select_language_hi),
            defaultText = context.resources.getString(R.string.default_language_hi),
            otherLanguages = context.resources.getString(R.string.other_languages_hi)
        )
    )

    languageList.add(
        Language(
            id = 4,
            name = context.resources.getString(R.string.italian),
            code ="it",
            image = R.drawable.flag_italian,
            nativeName = context.resources.getString(R.string.italian_native),
            buttonText = context.resources.getString(R.string.done_italian),
            titleText = context.resources.getString(R.string.select_language_it),
            defaultText = context.resources.getString(R.string.default_language_it),
            otherLanguages = context.resources.getString(R.string.other_languages_it)
        )
    )

    languageList.add(
        Language(
           id = 5,
           name = context.resources.getString(R.string.japanese),
           code ="ja",
           image = R.drawable.flag_japanese,
           nativeName = context.resources.getString(R.string.japanese_native),
           buttonText = context.resources.getString(R.string.done_japanese),
              titleText = context.resources.getString(R.string.select_language_ja),
                defaultText = context.resources.getString(R.string.default_language_ja),
                otherLanguages = context.resources.getString(R.string.other_languages_ja)
        )
    )

    languageList.add(
        Language(
            id = 6,
            name = context.resources.getString(R.string.portuguese),
            code ="pt",
            image = R.drawable.flag_portuguese,
            nativeName = context.resources.getString(R.string.portuguese_native),
            buttonText = context.resources.getString(R.string.done_portuguese),
            titleText = context.resources.getString(R.string.select_language_pt),
            defaultText = context.resources.getString(R.string.default_language_pt),
            otherLanguages = context.resources.getString(R.string.other_languages_pt)
        )
    )

    languageList.add(
        Language(
            id = 7,
            name = context.resources.getString(R.string.german),
            code ="de",
            image = R.drawable.flag_german,
            nativeName = context.resources.getString(R.string.german_native),
            buttonText = context.resources.getString(R.string.done_german),
            titleText = context.resources.getString(R.string.select_language_de),
            defaultText = context.resources.getString(R.string.default_language_de),
            otherLanguages = context.resources.getString(R.string.other_languages_de)
        )
    )

    languageList.add(
        Language(
            id = 8,
            name = context.resources.getString(R.string.dutch),
            code ="nl",
            image = R.drawable.flag_dutch,
            nativeName = context.resources.getString(R.string.dutch_native),
            buttonText = context.resources.getString(R.string.done_dutch),
            titleText = context.resources.getString(R.string.select_language_nl),
            defaultText = context.resources.getString(R.string.default_language_nl),
            otherLanguages = context.resources.getString(R.string.other_languages_nl)
        )
    )

    languageList.add(
        Language(
            id = 9,
            name = context.resources.getString(R.string.french),
            code ="fr",
            image = R.drawable.flag_french,
            nativeName = context.resources.getString(R.string.french_native),
            buttonText = context.resources.getString(R.string.done_french),
            titleText = context.resources.getString(R.string.select_language_fr),
            defaultText = context.resources.getString(R.string.default_language_fr),
            otherLanguages = context.resources.getString(R.string.other_languages_fr)
        )
    )

    languageList.add(
        Language(
            id = 10,
            name = context.resources.getString(R.string.arabic),
            code ="ar",
            image = R.drawable.flag_arabic,
            nativeName = context.resources.getString(R.string.arabic_native),
            buttonText = context.resources.getString(R.string.done_arabic),
            titleText = context.resources.getString(R.string.select_language_ar),
            defaultText = context.resources.getString(R.string.default_language_ar),
            otherLanguages = context.resources.getString(R.string.other_languages_ar)
        )
    )

    languageList.add(
        Language(
            id = 11,
            name = context.resources.getString(R.string.polish),
            code ="pl",
            image = R.drawable.flag_polish,
            nativeName = context.resources.getString(R.string.polish_native),
            buttonText = context.resources.getString(R.string.done_polish),
            titleText = context.resources.getString(R.string.select_language_pl),
            defaultText = context.resources.getString(R.string.default_language_pl),
            otherLanguages = context.resources.getString(R.string.other_languages_pl)
        )
    )

    languageList.add(
        Language(
            id = 12,
            name = context.resources.getString(R.string.turkish),
            code ="tr",
            image = R.drawable.flag_turkish,
            nativeName = context.resources.getString(R.string.turkish_native),
            buttonText = context.resources.getString(R.string.done_turkish),
            titleText = context.resources.getString(R.string.select_language_tr),
            defaultText = context.resources.getString(R.string.default_language_tr),
            otherLanguages = context.resources.getString(R.string.other_languages_tr)
        )
    )


    return languageList
}