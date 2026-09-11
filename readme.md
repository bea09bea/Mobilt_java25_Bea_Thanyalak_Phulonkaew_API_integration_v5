# GitScope

Idén med **GitScope** är att ge en snabb och tydlig överblick över en utvecklares erfarenhet, tekniska kompetens och projekt.

Användaren kan söka efter GitHub-användare och få relevant information samlad på ett ställe, utan att behöva navigera mellan flera olika sidor.

## Filstruktur

```text
com.bea.gitscope/
│
├── adapter/
│   ├── FavoriteAdapter.kt
│   └── GithubUserAdapter.kt
│
├── model/
│   ├── GitHubSearchResponse.kt
│   ├── GitHubUser.kt
│   └── GithubSearchUser.kt
│
├── network/
│   ├── GitHubApi.kt
│   └── RetrofitClient.kt
│
├── LoginActivity.kt
├── RegisterActivity.kt
├── MainActivity.kt
├── SearchFragment.kt
└── HomeFragment.kt
```
