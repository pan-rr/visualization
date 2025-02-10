import service from "../utils/request";

export async function taskMetrics(metric) {
    return await service.post('engine/portal/metric/taskMetrics', metric)
}

