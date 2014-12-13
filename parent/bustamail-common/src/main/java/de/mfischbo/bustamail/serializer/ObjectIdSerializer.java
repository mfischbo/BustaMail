package de.mfischbo.bustamail.serializer;

import java.io.IOException;

import org.bson.types.ObjectId;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class ObjectIdSerializer extends JsonSerializer<ObjectId> {

	@Override
	public void serialize(ObjectId arg0, JsonGenerator arg1,
			SerializerProvider arg2) throws IOException,
			JsonGenerationException {
		arg1.writeString(arg0.toString());
	}
}
