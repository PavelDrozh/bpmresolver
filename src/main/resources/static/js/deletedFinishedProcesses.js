document.addEventListener('DOMContentLoaded', function () {
    var filterForm = document.getElementById('deletedProcessesFilterForm');
    if (!filterForm) {
        return;
    }

    function setPageAndSubmit(page) {
        var pageInput = document.getElementById('pageInput');
        if (pageInput) {
            pageInput.value = String(page);
        }
        if (typeof filterForm.requestSubmit === 'function') {
            filterForm.requestSubmit();
        } else {
            filterForm.submit();
        }
    }

    var pagerButtons = document.querySelectorAll('[data-page]');
    for (let pagerButton of pagerButtons) {
        pagerButton.addEventListener('click', function (e) {
            e.preventDefault();
            var pageAttr = this.getAttribute('data-page');
            if (pageAttr === null || pageAttr === undefined) {
                return;
            }
            var p = Number.parseInt(pageAttr, 10);
            if (!Number.isNaN(p)) {
                setPageAndSubmit(p);
            }
        });
    }

    filterForm.addEventListener('submit', function () {
        var elements = filterForm.querySelectorAll('input[name], select[name], textarea[name]');
        for (const element of elements) {
            var el = element;
            if (el.name === 'page' || el.name === 'size') {
                continue;
            }
            var value = (el.value || '').trim();
            if (value === '') {
                el.disabled = true;
            }
        }
    });

    function getRowCheckboxes() {
        return document.querySelectorAll('input.proc-select[type="checkbox"]');
    }

    function updateDeleteButtonState() {
        var btn = document.getElementById('deleteBtn');
        if (!btn) {
            return;
        }
        var boxes = getRowCheckboxes();
        var anyChecked = false;
        for (const b of boxes) {
            if (b.checked) {
                anyChecked = true;
                break;
            }
        }
        btn.disabled = !anyChecked;
    }

    function updateSelectAllButtonLabel() {
        var btn = document.getElementById('selectAllBtn');
        if (!btn) {
            return;
        }
        var boxes = getRowCheckboxes();
        if (!boxes || boxes.length === 0) {
            btn.textContent = 'Выбрать все';
            return;
        }
        var allChecked = true;
        for (const b of boxes) {
            if (!b.checked) {
                allChecked = false;
                break;
            }
        }
        btn.textContent = allChecked ? 'Снять выбор' : 'Выбрать все';
    }

    var selectAllBtn = document.getElementById('selectAllBtn');
    if (selectAllBtn) {
        selectAllBtn.addEventListener('click', function (e) {
            e.preventDefault();
            var boxes = getRowCheckboxes();
            if (!boxes || boxes.length === 0) {
                return;
            }
            var shouldCheck = false;
            for (const b of boxes) {
                if (!b.checked) {
                    shouldCheck = true;
                    break;
                }
            }
            for (const b2 of boxes) {
                b2.checked = shouldCheck;
            }
            updateSelectAllButtonLabel();
            updateDeleteButtonState();
        });
    }

    var rowBoxes = getRowCheckboxes();
    for (const rb of rowBoxes) {
        rb.addEventListener('change', function () {
            updateSelectAllButtonLabel();
            updateDeleteButtonState();
        });
    }

    var deleteForm = document.getElementById('deleteForm');
    if (deleteForm) {
        deleteForm.addEventListener('submit', async function (e) {
            e.preventDefault();

            var boxes = getRowCheckboxes();
            var selected = [];
            for (const b of boxes) {
                if (b.checked) {
                    var id = b.getAttribute('data-id');
                    if (id) {
                        var n = Number.parseInt(id, 10);
                        if (!Number.isNaN(n)) {
                            selected.push(n);
                        }
                    }
                }
            }

            if (selected.length === 0) {
                updateDeleteButtonState();
                return;
            }

            var confirmText = 'Удалить выбранные записи (' + selected.length + ')?';
            if (!window.confirm(confirmText)) {
                return;
            }

            var res = await fetch('/api/finished-processes/delete-batch', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ ids: selected })
            });

            if (!res.ok) {
                alert('Ошибка удаления: HTTP ' + res.status);
                return;
            }

            window.location.reload();
        });
    }

    updateSelectAllButtonLabel();
    updateDeleteButtonState();
});
