package dto.page

data class PageRequest(
    val page: Int,
    val size: Int
) {
    companion object {
        const val MAX_SIZE = 100
        const val DEFAULT_SIZE = 10
        const val DEFAULT_PAGE = 1
    }
    
    init {
        require(page > 0) { "Page must be greater than 0" }
        require(size > 0) { "Size must be greater than 0" }
        require(size <= MAX_SIZE) { "Size must not exceed $MAX_SIZE" }
    }
    
    val offset: Long get() = ((page - 1) * size).toLong()
}