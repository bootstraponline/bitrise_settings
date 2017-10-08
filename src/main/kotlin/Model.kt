/*

{
    "data": [
        {
            "slug": "78caf52649a087fc",
            "title": "iOS JavaScript WIP",
            "project_type": "other",
            "provider": "github",
            "repo_owner": "instructure",
            "repo_url": "git@github.com:instructure/ios.git",
            "repo_slug": "ios",
            "is_disabled": false
        },
    "paging": {
        "total_item_count": 44,
        "page_item_limit": 50
    }
}

*/

data class AppObject(
        val slug: String,
        val title: String,
        val project_type: String,
        val provider: String,
        val repo_owner: String,
        val repo_url: String,
        val repo_slug: String,
        val is_disabled: Boolean
)

data class Paging(
        val total_item_count: Int,
        val page_item_limit: Int
)

data class AppData(
        val data: List<AppObject>,
        val paging: Paging
)
