package com.visualization.manager;

import com.visualization.factory.InputFactory;
import com.visualization.factory.OutputFactory;
import com.visualization.view.base.View;
import com.visualization.view.base.ViewConf;
import com.visualization.view.in.InputView;
import com.visualization.view.out.OutputView;
import org.springframework.util.CollectionUtils;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class ViewManager {

    private final List<InputView> inList;

    private final OutputView out;

    /**
     * 设置提供联邦查询的数据源
     */
    private final DataSource federationDataSource;


    private Map<String, Object> tableName;

    private final int id;

    public ViewManager(List<ViewConf> inView, ViewConf outView, DataSource federationDataSource) {
        if (CollectionUtils.isEmpty(inView)) throw new RuntimeException("输入表的配置为空");
        id = SessionManager.getId();
        this.federationDataSource = federationDataSource;
        this.inList = inView.stream().map((viewConf ->
                InputFactory.produce(viewConf, this)
        )).collect(Collectors.toList());
        out = OutputFactory.produce(outView, this);
        prepareTableName();
    }

    private void prepareTableName() {
        tableName = new HashMap<>();
        this.inList.forEach(view -> view.registerTableName(tableName));
        out.setTableNames(tableName);
    }


    public int getId() {
        return id;
    }

    public DataSource getFederationDataSource() {
        return federationDataSource;
    }

    public void execute() {
        try {
            CompletableFuture.allOf(inList.stream().map((in) -> CompletableFuture.runAsync(in::generate)).toArray(CompletableFuture[]::new)).join();
            out.generate();
        }finally {
            release(() -> inList.forEach(View::destroy));
            release(out::destroy);
            SessionManager.returnId(id);
        }
    }

    private void release(Runnable r) {
        try {
            r.run();
        } catch (Exception e) {
            // do nothing;
        }
    }


}
