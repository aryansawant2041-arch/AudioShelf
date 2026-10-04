package com.example.audioshelf

data class LibriVoxResponse(
    val books: List<LibriVoxBook>?
)

data class LibriVoxBook(
    val id: String,
    val title: String,
    val authors: List<LibriVoxAuthor>?,
    val totaltime: String?,
    val num_sections: String?,
    val url_librivox: String?,
    val coverart_thumbnail: String?,
    val language: String?,
    val genres: List<LibriVoxGenre>?,
    val sections: List<LibriVoxSection>?
)

data class LibriVoxAuthor(
    val first_name: String?,
    val last_name: String?
)

data class LibriVoxGenre(
    val id: String?,
    val name: String?
)

data class LibriVoxSection(
    val id: String?,
    val section_number: String?,
    val title: String?,
    val listen_url: String?,
    val language: String?,
    val playtime: String?
)