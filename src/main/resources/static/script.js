async function loadItems() {
    const loadingEl = document.getElementById('loading');
    const listEl = document.getElementById('item-list');

    try {
        const response = await fetch('/api/items');

        if (!response.ok) {
            throw new Error('サーバーからのデータ取得に失敗しました');
        }

        const items = await response.json();

        loadingEl.style.display = 'none';

        items.forEach(item => {
            const li = document.createElement('li');
            li.className = 'item-card';

            const formattedDate = formatDate(item.fetchedAt);

            li.innerHTML = `
                <span class="item-site-name">${escapeHtml(item.siteName)}</span>
                <a class="item-title" href="${escapeHtml(item.itemUrl)}" target="_blank" rel="noopener noreferrer">
                    ${escapeHtml(item.title)}
                </a>
                <span class="item-date">${formattedDate}</span>
            `;

            listEl.appendChild(li);
        });

    } catch (error) {
        loadingEl.textContent = 'データの読み込みに失敗しました。時間をおいて再度お試しください。';
        console.error(error);
    }
}

function formatDate(isoString) {
    const date = new Date(isoString);
    return date.toLocaleString('ja-JP', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
    });
}

function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

loadItems();