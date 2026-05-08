let chart;

function renderChart(data) {

    const ctx = document
        .getElementById('graphs')
        .getContext('2d');

    const labels = data.map(item => item.categoria);

    const valores = data.map(item => item.quantidade);

    if (chart) {
        chart.destroy();
    }

    chart = new Chart(ctx, {

        type: 'bar',

        data: {

            labels: labels,

            datasets: [{

                label: 'Reclamações',

                data: valores
            }]
        },

        options: {

            responsive: true
        }
    });
}

function voltar() {

    javaApp.seeComplaints();
}