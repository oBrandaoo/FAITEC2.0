function loadCategories(categorias) {

    const select = document.getElementById("category");

    categorias.forEach(categoria => {

        const option = document.createElement("option");

        option.value = categoria;
        option.textContent = categoria;

        select.appendChild(option);
    });
}

function submitComplaint() {

    const categoria =
        document.getElementById("category").value;

    const endereco =
        document.getElementById("location").value;

    const descricao =
        document.getElementById("description").value;

    javaApp.submitComplaint(
        categoria,
        endereco,
        descricao
    );
}

function goStart() {

    javaApp.goStart();
}

function showSuccess() {

    document.getElementById(
        "successMessage"
    ).innerText = "Reclamação enviada com sucesso!";
}