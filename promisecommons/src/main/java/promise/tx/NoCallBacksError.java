/*
 *
 *  * Copyright 2017, Peter Vincent
 *  * Licensed under the Apache License, Version 2.0, Promise.
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  * Unless required by applicable law or agreed to in writing,
 *  * software distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package promise.tx;

/**
 * Created by octopus on 10/16/16.
 */
public class NoCallBacksError extends Exception {
    public NoCallBacksError(String message) {
        super(message);
    }
    public NoCallBacksError() {
        this("No callbacks Defined, exiting task");
    }
    protected void show() {
        printStackTrace();
    }
}
