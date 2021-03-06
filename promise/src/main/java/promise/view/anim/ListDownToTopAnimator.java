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

package promise.view.anim;

import android.view.View;

import promise.cac.anim.Anim;
import promise.cac.anim.AnimDuration;

/**
 * Created by octopus on 10/1/16.
 */
public class ListDownToTopAnimator extends ListAnimator {
    public ListDownToTopAnimator(View view) {
        super(view);
        super.setAnim(
                Anim.Slide.slideInUp());
        super.setAnimDuration(
                AnimDuration.ofSecond());
    }
}
