/**
 * Copyright (c) 2014 Richard Warburton (richard.warburton@gmail.com)
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 **/
package com.insightfullogic.honest_profiler.ports.javafx.controller;

import static com.insightfullogic.honest_profiler.ports.javafx.model.filter.FilterType.STRING;
import static com.insightfullogic.honest_profiler.ports.javafx.model.filter.FilterType.THREAD_SAMPLE;
import static com.insightfullogic.honest_profiler.ports.javafx.model.filter.FilterType.TIME_SHARE;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.INFO_BUTTON_COLLAPSEALLALL;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.INFO_BUTTON_EXPANDALL;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.INFO_BUTTON_FILTER;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.INFO_BUTTON_QUICKFILTER;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.INFO_INPUT_QUICKFILTER;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.INFO_TABLE_TREE;
import static com.insightfullogic.honest_profiler.ports.javafx.util.TreeUtil.expandFully;
import static com.insightfullogic.honest_profiler.ports.javafx.view.Rendering.renderMethod;
import static com.insightfullogic.honest_profiler.ports.javafx.view.Rendering.renderPercentage;

import java.util.function.Function;

import com.insightfullogic.honest_profiler.core.profiles.Profile;
import com.insightfullogic.honest_profiler.core.profiles.ProfileNode;
import com.insightfullogic.honest_profiler.ports.javafx.model.ApplicationContext;
import com.insightfullogic.honest_profiler.ports.javafx.model.filter.FilterType;
import com.insightfullogic.honest_profiler.ports.javafx.model.task.CopyAndFilterProfile;
import com.insightfullogic.honest_profiler.ports.javafx.util.TreeUtil;
import com.insightfullogic.honest_profiler.ports.javafx.view.cell.MethodNameTreeTableCell;
import com.insightfullogic.honest_profiler.ports.javafx.view.cell.TreeViewCell;
import com.insightfullogic.honest_profiler.ports.javafx.view.tree.MethodNodeAdapter;
import com.insightfullogic.honest_profiler.ports.javafx.view.tree.RootNodeAdapter;
import com.insightfullogic.honest_profiler.ports.javafx.view.tree.ThreadNodeAdapter;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableColumn.CellDataFeatures;
import javafx.scene.control.TreeTableView;

public class TreeViewController extends ProfileViewController<Profile>
{
    @FXML
    private Button filterButton;
    @FXML
    private Button expandAllButton;
    @FXML
    private Button collapseAllButton;
    @FXML
    private TextField quickFilterText;
    @FXML
    private Button quickFilterButton;

    @FXML
    private TreeTableView<ProfileNode> treeView;
    @FXML
    private TreeTableColumn<ProfileNode, String> methodColumn;
    @FXML
    private TreeTableColumn<ProfileNode, ProfileNode> percentColumn;
    @FXML
    private TreeTableColumn<ProfileNode, String> totalColumn;
    @FXML
    private TreeTableColumn<ProfileNode, String> selfColumn;

    private RootNodeAdapter rootNode;

    @Override
    @FXML
    protected void initialize()
    {
        super.initialize(
            profileContext -> profileContext.profileProperty(),
            filterButton,
            quickFilterButton,
            quickFilterText);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext)
    {
        super.setApplicationContext(applicationContext);
        initializeTable();
    }

    // Initialization Helper Methods

    private void initializeTable()
    {
        rootNode = new RootNodeAdapter(getFilterSpecification());
        treeView.setRoot(rootNode);

        methodColumn.setCellFactory(column -> new MethodNameTreeTableCell<>(appCtx()));
        methodColumn.setCellValueFactory(data -> buildProfileNodeCell(data.getValue()));

        totalColumn.setCellValueFactory(data -> wrapDouble(data, ProfileNode::getTotalTimeShare));
        selfColumn.setCellValueFactory(data -> wrapDouble(data, ProfileNode::getSelfTimeShare));

        percentColumn.setCellFactory(param -> new TreeViewCell());
    }

    // Helper Methods

    private StringProperty buildProfileNodeCell(TreeItem<ProfileNode> treeItem)
    {
        String text = "";

        if (treeItem instanceof ThreadNodeAdapter)
        {
            ThreadNodeAdapter adapter = (ThreadNodeAdapter) treeItem;
            String name = adapter.getThreadName();

            StringBuilder builder = new StringBuilder("Thread").append(' ');
            builder.append(
                (adapter.getThreadId() < 0) ? "Unknown [" + adapter.getThreadId() + "]"
                    : adapter.getThreadId());

            builder.append(' ')
                .append((name == null || name.isEmpty()) ? "Unknown" : "[" + name + "]")
                .append(" (depth = ").append(adapter.getDepth()).append(", # samples = ")
                .append(adapter.getNrOfSamples()).append(")");

            text = builder.toString();
        }
        else if (treeItem instanceof MethodNodeAdapter)
        {
            text = renderMethod(treeItem.getValue().getFrameInfo());
        }

        return new ReadOnlyStringWrapper(text);
    }

    private StringProperty wrapDouble(CellDataFeatures<ProfileNode, String> data,
        Function<ProfileNode, Double> accessor)
    {
        return new ReadOnlyStringWrapper(
            data.getValue().getValue() != null
                ? renderPercentage(accessor.apply(data.getValue().getValue())) : "");
    }

    // AbstractController Implementation

    @Override
    protected void initializeInfoText()
    {
        info(filterButton, INFO_BUTTON_FILTER);
        info(expandAllButton, INFO_BUTTON_EXPANDALL);
        info(collapseAllButton, INFO_BUTTON_COLLAPSEALLALL);
        info(quickFilterText, INFO_INPUT_QUICKFILTER);
        info(quickFilterButton, INFO_BUTTON_QUICKFILTER);
        info(treeView, INFO_TABLE_TREE);
    }

    @Override
    protected void initializeHandlers()
    {
        expandAllButton.setOnAction(event -> expandFully(treeView.getRoot()));

        collapseAllButton.setOnAction(
            event -> treeView.getRoot().getChildren().stream().forEach(TreeUtil::collapseFully));
    }

    // AbstractViewController Implementation

    @Override
    protected void refresh()
    {
        CopyAndFilterProfile task = new CopyAndFilterProfile(
            getTarget(),
            getAdjustedProfileFilter());
        task.setOnSucceeded(state -> rootNode.update(task.getValue().getTrees()));
        appCtx().getExecutorService().execute(task);
    }

    @Override
    protected FilterType[] getAllowedFilterTypes()
    {
        return new FilterType[]
        { STRING, THREAD_SAMPLE, TIME_SHARE };
    }
}
