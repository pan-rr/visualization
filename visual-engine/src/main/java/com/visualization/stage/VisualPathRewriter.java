package com.visualization.stage;

import com.visualization.enums.VisualContextKey;
import com.visualization.model.file.FilePathMapping;
import com.visualization.repository.file.FilePathMappingRepository;
import com.visualization.runtime.VPath;
import com.visualization.utils.FilePathUtil;
import com.visualization.utils.ShortLinkUtil;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class VisualPathRewriter {

    @Resource
    private FilePathMappingRepository filePathMappingRepository;

    public VPath rewrite(VPath vPath) {
        vPath.path = getRealPath(vPath);
        return vPath;
    }

    private String getRealPath(VPath vPath) {
        if (vPath == null) return null;
        String logicPath = computeOSSPath(vPath);
        Optional<FilePathMapping> optional = filePathMappingRepository.findById(ShortLinkUtil.zipToInt(logicPath));
        if (optional.isPresent()) {
            return optional.get().getSourcePath();
        }
        return logicPath;
    }

    private String computeOSSPath(VPath vPath) {
        VETContext context = (VETContext) vPath.context;
        String path = vPath.path;
        Map<String, Object> params = new HashMap<>();
        if (path.startsWith(FilePathUtil.SPACE_SHARE)) {
            params.put(FilePathUtil.SPACE_SHARE, true);
        }
        params.put(VisualContextKey.space.name(), context.getValue(VisualContextKey.space.name(), String.class));
        params.put(VisualContextKey.instanceId.name(), context.getValue(VisualContextKey.instanceId.name(), Long.class));
        params.put(VisualContextKey.templateId.name(), context.getValue(VisualContextKey.templateId.name(), Long.class));
        params.put("filePath", path);
        return FilePathUtil.getStoragePath(params);
    }
}
