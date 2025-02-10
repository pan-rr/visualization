import service from "../utils/request";

export async function validate(sql) {
    return await service.get('engine/portal/table/validate', { params: { sql } })
}

