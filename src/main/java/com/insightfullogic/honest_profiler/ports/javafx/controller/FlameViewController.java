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

import com.insightfullogic.honest_profiler.core.profiles.FlameGraph;
import com.insightfullogic.honest_profiler.ports.javafx.model.ProfileContext;
import com.insightfullogic.honest_profiler.ports.javafx.model.filter.FilterType;
import com.insightfullogic.honest_profiler.ports.javafx.view.FlameGraphCanvas;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

public class FlameViewController extends ProfileViewController<FlameGraph>
{
    @FXML
    private VBox rootContainer;

    private FlameGraphCanvas flameView = new FlameGraphCanvas();

    @Override
    @FXML
    protected void initialize()
    {
        super.initialize(profileContext -> profileContext.flameGraphProperty(), null, null, null);

        rootContainer.getChildren().add(flameView);
    }

    public void refreshFlameView()
    {
        if (!flameView.widthProperty().isBound())
        {
            flameView.setWidth(rootContainer.getWidth());
            flameView.setHeight(rootContainer.getHeight());

            flameView.refresh();

            flameView.heightProperty().bind(rootContainer.heightProperty());
            flameView.widthProperty().bind(rootContainer.widthProperty());

            flameView.heightProperty()
                .addListener((property, oldValue, newValue) -> refreshFlameView());
            flameView.widthProperty()
                .addListener((property, oldValue, newValue) -> refreshFlameView());
        }
        else
        {
            flameView.refresh();
        }
    }

    // AbstractController Implementation

    @Override
    protected void initializeInfoText()
    {
        // NOOP
    }

    @Override
    protected void initializeHandlers()
    {
        // NOOP
    }

    // AbstractViewController Implementation

    @Override
    protected void refresh()
    {
        flameView.accept(getTarget());
    }

    @Override
    protected <C> void setColumnHeader(C column, String title, ProfileContext context)
    {
        // NOOP
    }

    @Override
    protected FilterType[] getAllowedFilterTypes()
    {
        return null;
    }
}
