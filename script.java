
const form = document.getElementById("expenseForm");

let expenses = JSON.parse(
    localStorage.getItem("expenses")
) || [];

let chart;

// Set today's date
document.getElementById("date").value =
    new Date().toLocaleDateString("en-CA");

form.addEventListener("submit", function(event) {
    event.preventDefault();

    const title = document.getElementById("title").value.trim();
    const amount = Number(document.getElementById("amount").value);
    const category = document.getElementById("category").value;
    const date = document.getElementById("date").value;

    if (!title || !category || !date ||
        !Number.isFinite(amount) || amount <= 0) {
        alert("Please enter valid expense details.");
        return;
    }

    const expense = {
        id: Date.now(),
        title,
        amount,
        category,
        date
    };

    expenses.push(expense);

    saveExpenses();
    form.reset();

    document.getElementById("date").value =
        new Date().toLocaleDateString("en-CA");
});

function saveExpenses() {
    localStorage.setItem(
        "expenses",
        JSON.stringify(expenses)
    );

    displayExpenses();
    updateSummary();
    updateChart();
}

function displayExpenses() {
    const list = document.getElementById("expenseList");
    list.innerHTML = "";

    expenses.forEach(function(expense) {
        const row = document.createElement("tr");

        const values = [
            expense.title,
            expense.category,
            "₹" + expense.amount.toFixed(2),
            expense.date
        ];

        values.forEach(function(value) {
            const cell = document.createElement("td");
            cell.textContent = value;
            row.appendChild(cell);
        });

        const actionCell = document.createElement("td");
        const deleteButton = document.createElement("button");

        deleteButton.textContent = "Delete";
        deleteButton.className = "delete-btn";

        deleteButton.addEventListener("click", function() {
            expenses = expenses.filter(function(item) {
                return item.id !== expense.id;
            });

            saveExpenses();
        });

        actionCell.appendChild(deleteButton);
        row.appendChild(actionCell);
        list.appendChild(row);
    });
}

function updateSummary() {
    const total = expenses.reduce(
        (sum, item) => sum + item.amount, 0
    );

    const currentMonth = new Date().toISOString().slice(0, 7);

    const monthly = expenses
        .filter(item => item.date.startsWith(currentMonth))
        .reduce((sum, item) => sum + item.amount, 0);

    document.getElementById("totalExpense").textContent =
        "₹" + total.toFixed(2);

    document.getElementById("monthlyExpense").textContent =
        "₹" + monthly.toFixed(2);

    document.getElementById("totalRecords").textContent =
        expenses.length;
}

function updateChart() {
    const categories = {};

    expenses.forEach(function(item) {
        categories[item.category] =
            (categories[item.category] || 0) + item.amount;
    });

    const ctx = document.getElementById("expenseChart");

    if (chart) {
        chart.destroy();
    }

    chart = new Chart(ctx, {
        type: "pie",
        data: {
            labels: Object.keys(categories),
            datasets: [{
                data: Object.values(categories)
            }]
        },
        options: {
            responsive: true,
            plugins: {
                legend: {
                    position: "bottom"
                }
            }
        }
    });
}

// Display saved expenses when website opens
displayExpenses();
updateSummary();
updateChart();
