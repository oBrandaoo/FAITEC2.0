function renderTable(data) {

    const tbody =
        document.querySelector(
            "#complaintsTable tbody"
        );

    tbody.innerHTML = "";

    data.forEach((item, index) => {

        tbody.innerHTML += `

            <tr>

                <td>${item.categoria}</td>
                <td>${item.local}</td>
                <td>${item.descricao}</td>
                <td>${item.status}</td>
                <td>${item.data}</td>

                <td>

                    <button class="action-btn"
                            onclick="resolver(${index})">

                        Resolver

                    </button>

                </td>

            </tr>
        `;
    });
}

function resolver(index) {

    javaApp.resolver(index);
}