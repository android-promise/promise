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
package promise.promisenet.net;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import promise.promisenet.http.Body;
import promise.promisenet.http.Field;
import promise.promisenet.http.FieldMap;
import promise.promisenet.http.Header;
import promise.promisenet.http.HeaderMap;
import promise.promisenet.http.Part;
import promise.promisenet.http.PartMap;
import promise.promisenet.http.Path;
import promise.promisenet.http.Query;
import promise.promisenet.http.QueryMap;

/**
 * Convert objects to and from their representation in HTTP. Instances are created by {@linkplain
 * Factory a factory} which is {@linkplain FastParserEngine.Builder#addConverterFactory(Factory) installed}
 * into the {@link FastParserEngine} instance.
 */
public interface Converter<F, T> {
  T convert(F value) throws IOException;

  /** Creates {@link Converter} instances based on a type and target usage. */
  abstract class Factory {
    /**
     * Returns a {@link Converter} for converting an HTTP response body to {@code type}, or null if
     * {@code type} cannot be handled by this factory. This is used to create converters for
     * response types such as {@code SimpleResponse} from a {@code Call<SimpleResponse>}
     * declaration.
     */
    public @Nullable
    Converter<ResponseBody, ?> responseBodyConverter(Type type,
                                                     Annotation[] annotations, FastParserEngine fastParserEngine) {
      return null;
    }

    /**
     * Returns a {@link Converter} for converting {@code type} to an HTTP request body, or null if
     * {@code type} cannot be handled by this factory. This is used to create converters for types
     * specified by {@link Body @Body},
     * {@link Part @Part},
     * and {@link PartMap @PartMap}
     * values.
     */
    public @Nullable Converter<?, RequestBody> requestBodyConverter(Type type,
        Annotation[] parameterAnnotations, Annotation[] methodAnnotations, FastParserEngine fastParserEngine) {
      return null;
    }

    /**
     * Returns a {@link Converter} for converting {@code type} to a {@link String}, or null if
     * {@code type} cannot be handled by this factory. This is used to create converters for types
     * specified by {@link Field @Field}, {@link FieldMap @FieldMap} values,
     * {@link Header @Header},
     * {@link HeaderMap @HeaderMap}, {@link Path @Path},
     * {@link Query @Query}, and {@link QueryMap @QueryMap} values.
     */
    public @Nullable Converter<?, String> stringConverter(Type type, Annotation[] annotations,
        FastParserEngine fastParserEngine) {
      return null;
    }

    /**
     * Extract the upper bound of the generic parameter at {@code index} from {@code type}. For
     * example, index 1 of {@code Map<String, ? extends Runnable>} returns {@code Runnable}.
     */
    protected static Type getParameterUpperBound(int index, ParameterizedType type) {
      return Utils.getParameterUpperBound(index, type);
    }

    /**
     * Extract the raw class type from {@code type}. For example, the type representing
     * {@code List<? extends Runnable>} returns {@code List.class}.
     */
    protected static Class<?> getRawType(Type type) {
      return Utils.getRawType(type);
    }
  }
}
