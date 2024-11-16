package com.visualization.manager;

import com.visualization.factory.InputFactory;
import com.visualization.factory.OutputFactory;
import com.visualization.handler.SpringContextHandler;
import com.visualization.handler.rewrite.ViewRewriteHandler;
import com.visualization.view.base.*;
import com.visualization.view.in.InputView;
import com.visualization.view.out.OutputView;
import org.springframework.context.ApplicationContext;
import org.springframework.util.CollectionUtils;

import javax.sql.DataSource;
import java.util.*;
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

    private ViewContext context;

    public ViewManager(VisualStage stage) {
        List<ViewConf> inView = stage.getInputView();
        if (CollectionUtils.isEmpty(inView)) throw new RuntimeException("输入表的配置为空");
        ApplicationContext ctx = SpringContextHandler.getCtx();
        DataSource db = ctx.getBean("db", DataSource.class);
        Collection<ViewRewriteHandler> viewRewriteHandlers = ctx.getBeansOfType(ViewRewriteHandler.class).values();
        stage.filter(viewRewriteHandlers);
        id = SessionManager.getId();
        this.federationDataSource = db;
        this.context = new ViewContext(stage.getVisualContext());
        this.inList = inView.stream().map((viewConf -> InputFactory.produce(viewConf, this))).collect(Collectors.toList());
        out = OutputFactory.produce(stage.getOutputView(), this);
        prepareTableName();
        injectViewContext();
    }

    private void injectViewContext() {
        LinkedList<BaseView> list = new LinkedList<>();
        this.inList.forEach(i -> list.add((BaseView) i));
        list.add((BaseView) out);
        list.forEach(i -> i.setContext(this.context));
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
        } catch (Throwable e) {
            throw new RuntimeException(e);
        } finally {
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
