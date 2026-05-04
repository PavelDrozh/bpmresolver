document.addEventListener('DOMContentLoaded', function () {
    var form = document.getElementById('instancesFilterForm');
    if (!form) {
        return;
    }

    function setPageAndSubmit(page) {
        var pageInput = document.getElementById('pageInput');
        if (pageInput) {
            pageInput.value = String(page);
        }
        if (typeof form.requestSubmit === 'function') {
            form.requestSubmit();
        } else {
            form.submit();
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

    form.addEventListener('submit', function () {
        var elements = form.querySelectorAll('input[name], select[name], textarea[name]');
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
        return document.querySelectorAll('input.instance-select[type="checkbox"]');
    }

    function updateFinishButtonState() {
        var btn = document.getElementById('finishBtn');
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
            updateFinishButtonState();
        });
    }

    var rowBoxes = getRowCheckboxes();
    for (const rb of rowBoxes) {
        rb.addEventListener('change', function () {
            updateSelectAllButtonLabel();
            updateFinishButtonState();
        });
    }

    var finishForm = document.getElementById('finishForm');
    if (finishForm) {
        finishForm.addEventListener('submit', function (e) {
            var existing = finishForm.querySelectorAll('input[name="id"]');
            for (const ex of existing) {
                ex.remove();
            }

            var boxes = getRowCheckboxes();
            var selected = [];
            for (const b of boxes) {
                if (b.checked) {
                    var id = b.getAttribute('data-id');
                    if (id) {
                        selected.push(id);
                    }
                }
            }

            if (selected.length === 0) {
                e.preventDefault();
                updateFinishButtonState();
                return;
            }

            for (const idValue of selected) {
                var input = document.createElement('input');
                input.type = 'hidden';
                input.name = 'id';
                input.value = idValue;
                finishForm.appendChild(input);
            }
        });
    }

    updateSelectAllButtonLabel();
    updateFinishButtonState();
});
