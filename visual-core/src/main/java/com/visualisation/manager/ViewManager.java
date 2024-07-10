package com.visualisation.manager;

import com.visualisation.factory.InputFactory;
import com.visualisation.factory.OutputFactory;
import com.visualisation.view.View;
import com.visualisation.view.ViewConf;
import com.visualisation.view.in.InputView;
import com.visualisation.view.out.OutputView;
import org.springframework.util.CollectionUtils;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class ViewManager {

    private List<InputView> inList;

    private OutputView out;

    /**
     * 设置提供联邦查询的数据源
     */
    private DataSource federationDataSource;


    private Map<String, Object> tableName;

    private int id;

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
        List<CompletableFuture<Void>> collect = inList.stream().map((in) -> CompletableFuture.runAsync(in::generate)).collect(Collectors.toList());
        CompletableFuture.allOf(collect.toArray(new CompletableFuture[0])).join();
        out.generate();
        inList.forEach(View::destroy);
        out.destroy();
        SessionManager.returnId(id);
    }


}
