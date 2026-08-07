package org.example.model.enums;

import java.util.Arrays;
import java.util.List;

public enum ComplaintSubcategory {

    BURACO_EM_VIA(ComplaintCategory.BURACO_RUA, "Buraco em via"),
    ASFALTO_DANIFICADO(ComplaintCategory.BURACO_RUA, "Asfalto danificado"),

    POSTE_APAGADO(ComplaintCategory.ILUMINACAO_PUBLICA, "Poste apagado"),
    LAMPADA_OSCILANDO(ComplaintCategory.ILUMINACAO_PUBLICA, "Lâmpada oscilando"),
    FIACAO_EXPOSTA(ComplaintCategory.ILUMINACAO_PUBLICA, "Fiação exposta"),

    COLETA_ATRASADA(ComplaintCategory.LIX0_ACUMULADO, "Coleta atrasada"),
    DESCARTE_IRREGULAR(ComplaintCategory.LIX0_ACUMULADO, "Descarte irregular"),
    ENTULHO_EM_VIA(ComplaintCategory.LIX0_ACUMULADO, "Entulho em via pública"),

    VAZAMENTO_ESGOTO(ComplaintCategory.ESGOTO, "Vazamento de esgoto"),
    BUEIRO_ENTUPIDO(ComplaintCategory.ESGOTO, "Bueiro entupido"),
    MAU_CHEIRO(ComplaintCategory.ESGOTO, "Mau cheiro"),

    PONTO_INSEGURO(ComplaintCategory.SEGURANCA, "Ponto inseguro"),
    VANDALISMO(ComplaintCategory.SEGURANCA, "Vandalismo"),
    AREA_SEM_MONITORAMENTO(ComplaintCategory.SEGURANCA, "Área sem monitoramento"),

    SEMAFORO_DEFEITO(ComplaintCategory.TRANSITO_MOBILIDADE, "Semáforo com defeito"),
    SINALIZACAO_AUSENTE(ComplaintCategory.TRANSITO_MOBILIDADE, "Sinalização ausente ou danificada"),
    FAIXA_PEDESTRES_APAGADA(ComplaintCategory.TRANSITO_MOBILIDADE, "Faixa de pedestres apagada"),
    VEICULO_ABANDONADO(ComplaintCategory.TRANSITO_MOBILIDADE, "Veículo abandonado"),
    CONGESTIONAMENTO_RECORRENTE(ComplaintCategory.TRANSITO_MOBILIDADE, "Congestionamento recorrente");

    private final ComplaintCategory category;
    private final String description;

    ComplaintSubcategory(ComplaintCategory category, String description) {
        this.category = category;
        this.description = description;
    }

    public ComplaintCategory getCategory() {
        return category;
    }

    public boolean belongsTo(ComplaintCategory category) {
        return this.category == category;
    }

    public static List<ComplaintSubcategory> forCategory(ComplaintCategory category) {
        if (category == null) {
            return List.of();
        }
        return Arrays.stream(values())
            .filter(subcategory -> subcategory.belongsTo(category)).toList();
    }

    @Override
    public String toString() {
        return description;
    }
}
