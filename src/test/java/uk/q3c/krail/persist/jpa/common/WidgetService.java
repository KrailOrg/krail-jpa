/*
 *
 *  * Copyright (c) 2016. David Sowerby
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 *  * the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 *  * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 *  * specific language governing permissions and limitations under the License.
 *
 */

package uk.q3c.krail.persist.jpa.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by David Sowerby on 08/04/15.
 */
public class WidgetService {
    private static Logger log = LoggerFactory.getLogger(WidgetService.class);

    public void msg(String msg) {
        log.info(msg);
    }

    public void writeWidget(Widget w) {
        log.info(w.getName());
    }
}
