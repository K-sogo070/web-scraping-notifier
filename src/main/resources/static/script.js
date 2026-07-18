let knownIds = new Set();
let allItems = [];
let currentFilter = 'all';

async function loadItems(isInitial = false) {
    const loadingEl = document.getElementById('loading');

    try {
        const response = await fetch('/api/items');

        if (!response.ok) {
            throw new Error('サーバーからのデータ取得に失敗しました');
        }

        const items = await response.json();

        if (isInitial) {
            loadingEl.style.display = 'none';
        }

        let hasNewItem = false;

        items.forEach(item => {
            if (!knownIds.has(item.id)) {
                knownIds.add(item.id);
                if (isInitial) {
                    allItems.push(item);
                } else {
                    allItems.unshift(item);
                }
                if (!isInitial) hasNewItem = true;
            }
        });

        if (isInitial || hasNewItem) {
            renderFilterBar();
            renderList();
        }

    } catch (error) {
        if (isInitial) {
            loadingEl.textContent = 'データの読み込みに失敗しました。時間をおいて再度お試しください。';
        }
        console.error(error);
    }
}

function renderFilterBar() {
    const filterBarEl = document.getElementById('filter-bar');
    const siteNames = [...new Set(allItems.map(item => item.siteName))];

    let html = `<button class="filter-chip ${currentFilter === 'all' ? 'active' : ''}" data-site="all">すべて</button>`;

    siteNames.forEach(siteName => {
        const isActive = currentFilter === siteName ? 'active' : '';
        html += `<button class="filter-chip ${isActive}" data-site="${escapeHtml(siteName)}">${escapeHtml(siteName)}</button>`;
    });

    filterBarEl.innerHTML = html;

    filterBarEl.querySelectorAll('.filter-chip').forEach(btn => {
        btn.addEventListener('click', () => {
            currentFilter = btn.dataset.site;
            renderFilterBar();
            renderList();
        });
    });
}

function renderList() {
    const listEl = document.getElementById('item-list');
    const filtered = currentFilter === 'all'
        ? allItems
        : allItems.filter(item => item.siteName === currentFilter);

    listEl.innerHTML = '';

    filtered.forEach(item => {
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

loadItems(true);
setInterval(() => loadItems(false), 30000);