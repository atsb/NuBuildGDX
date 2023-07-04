package ru.m210projects.Build.Render.GdxRender.Shaders;

public class WorldShader {

	public static final String vertex = "#ifdef GL_ES\n" //
			+ "#define LOWP lowp\n" //
			+ "    precision mediump float;\n" //
			+ "#else\n" //
			+ "#define LOWP\n" //
			+ "#endif\n" //
			+ "\n" //
			+ "attribute vec4 a_position;\n" //
			+ "attribute vec2 a_texCoord0;\n" //
			+ "attribute vec4 a_color;\n" //
			+ "\n" //
			+ "uniform mat4 u_modelView;\n" //
			+ "uniform mat4 u_projTrans;\n" //
			+ "uniform mat4 u_transform;\n" //
			+ "uniform mat3 u_texture_transform;\n" //
			+ "uniform bool u_mirror;\n" //
			+ "\n" //
			+ "varying LOWP float v_dist;\n" //
			+ "varying vec2 v_texCoords;\n" //
			+ "varying vec4 v_color;\n" //
			+ "\n" //
			+ "void main() {\n" //
			+ "    v_texCoords = vec2(u_texture_transform * vec3(a_texCoord0, 1.0));\n" //
			+ "    v_color = a_color;\n" //
			+ "    v_color.a = v_color.a * (255.0/254.0);\n" //
			+ "    vec4 mv = u_modelView * u_transform  * a_position;\n" //
			+ "    gl_Position = u_projTrans * u_transform * a_position;\n" //
			+ "    if(u_mirror)\n" //
			+ "        gl_Position.x *= -1.0;\n" //
			+ "    v_dist = mv.z / mv.w;\n" //
			+ "};\n" //
			+ ""; //

	public static final String fragment = "uniform sampler2D u_texture;\n" //
			+ "uniform sampler2D u_palette;\n" //
			+ "uniform sampler2D u_palookup;\n" //
			+ "\n" //
			+ "uniform int u_numshades;\n" //
			+ "uniform float u_visibility;\n" //
			+ "uniform int u_shade;\n" //
			+ "uniform bool u_draw255;\n" //
			+ "uniform float u_alpha;\n" //
			+ "\n" //
			+ "varying float v_dist;\n" //
			+ "varying vec2 v_texCoords;\n" //
			+ "\n" //
			+ "uniform mat4 u_invProjectionView;\n" //
			+ "uniform vec4 u_plane[2];\n" //
			+ "uniform vec4 u_viewport;\n" //
			+ "uniform int u_planeClipping;\n" //
			+ "\n" //
			+ "float getpalookup(int dashade) {\n" //
			+ "    float davis = v_dist * u_visibility;\n" //
			+ "    float shade = (min(max(float(dashade) + davis, 0.0), float(u_numshades - 1)));\n" //
			+ "    return shade / 64.0;\n" //
			+ "}\n" //
			+ "\n" //
			+ "vec4 getPos() {\n" //
			+ "    vec4 ndc;\n" //
			+ "	   vec2 xy = gl_FragCoord.xy - vec2(u_viewport.xy);" //
			+ "    ndc.xy = (2.0 * xy) / u_viewport.zw - 1.0;\n" //
			+ "    ndc.z = (2.0 * gl_FragCoord.z) - 1.0;\n" //
			+ "    ndc.w = 1.0;\n" //
			+ "    \n" //
			+ "    vec4 worldCoords = u_invProjectionView * ndc;\n" //
			+ "    worldCoords.xyz /= worldCoords.w;\n" //
			+ "    worldCoords.xyz *= vec3(512.0, 512.0, 8192.0); // BuildEngine coords scale\n" //
			+ "    worldCoords.w = 1.0;\n" //
			+ "    return worldCoords;\n" //
			+ "}\n" //
			+ "\n" //
			+ "bool isvisible() {\n" //
			+ "    vec4 pos = getPos();\n" //
			+ "    for(int i = 0; i < 2; i++) {\n" //
			+ "        if(dot(u_plane[i], pos) < 0.0)\n" //
			+ "            return false;\n" //
			+ "    }\n" //
			+ "    return true;\n" //
			+ "}\n" //
			+ "\n" //
			+ "void main() {  \n" //
			+ "    if((u_planeClipping == 1 && !isvisible()))\n" //
			+ "        discard;\n" //
			+ "\n" //
			+ "	   if(u_planeClipping == 2 && (gl_FragCoord.x < u_viewport.x || gl_FragCoord.x > u_viewport.z\n" //
			+ "		 || gl_FragCoord.y < u_viewport.w || gl_FragCoord.y > u_viewport.y))" //
			+ "		   discard;" //
			+ "\n" //
			+ "    float fi = texture2D(u_texture, v_texCoords).r;\n" //
			+ "    if(fi == 1.0) {\n" //
			+ "        if(!u_draw255) {\n" //
			+ "            if(u_alpha >= 0.5)\n" //
			+ "               discard;\n" //
			+ "            gl_FragColor = vec4(0.01);\n" //
			+ "            return;\n" //
			+ "        }\n" //
			+ "        fi -= 0.5 / 256.0;\n" //
			+ "    }\n" //
			+ "    float index = texture2D(u_palookup, vec2(fi, getpalookup(u_shade))).r;\n" //
			+ "    if(index == 1.0) index -= 0.5 / 256.0;\n" //
			+ "\n" //
			+ "    gl_FragColor = vec4(texture2D(u_palette, vec2(index, 0.0)).rgb, u_alpha);\n" //
			+ "}"; //

	public static final String fragmentRGB = "uniform sampler2D u_texture;\n" //
			+ "varying vec2 v_texCoords;\n" //
			+ "varying vec4 v_color;\n" //
			+ "\n" //
			+ "uniform mat4 u_invProjectionView;\n" //
			+ "uniform vec4 u_plane[2];\n" //
			+ "uniform vec4 u_viewport;\n" //
			+ "uniform vec4 u_color;\n" //
			+ "uniform int u_planeClipping;\n" //
			+ "uniform bool u_fogEnable;\n" //
			+ "uniform float u_fogEnd;\n" //
			+ "uniform float u_fogStart;\n" //
			+ "uniform vec3 u_fogColor;\n" //
			+ "\n" //
			+ "float calcFog(float dist) { \n" //
			+ "	   return clamp(1.0 - (u_fogEnd - dist) / (u_fogEnd - u_fogStart), 0.0, 1.0); \n" //
			+ "} \n" //
			+ "vec4 getPos() {\n" //
			+ "    vec4 ndc;\n" //
			+ "	   vec2 xy = gl_FragCoord.xy - vec2(u_viewport.xy);" //
			+ "    ndc.xy = (2.0 * xy) / u_viewport.zw - 1.0;\n" //
			+ "    ndc.z = (2.0 * gl_FragCoord.z) - 1.0;\n" //
			+ "    ndc.w = 1.0;\n" //
			+ "    \n" //
			+ "    vec4 worldCoords = u_invProjectionView * ndc;\n" //
			+ "    worldCoords.xyz /= worldCoords.w;\n" //
			+ "    worldCoords.xyz *= vec3(512.0, 512.0, 8192.0); // BuildEngine coords scale\n" //
			+ "    worldCoords.w = 1.0;\n" //
			+ "    return worldCoords;\n" //
			+ "}\n" //
			+ "\n" //
			+ "bool isvisible() {\n" //
			+ "    vec4 pos = getPos();\n" //
			+ "    for(int i = 0; i < 2; i++) {\n" //
			+ "        if(dot(u_plane[i], pos) < 0.0)\n" //
			+ "            return false;\n" //
			+ "    }\n" //
			+ "    return true;\n" //
			+ "}\n" //
			+ "\n" //
			+ "void main() {  \n" //
			+ "    if((u_planeClipping == 1 && !isvisible()))\n" //
			+ "        discard;\n" //
			+ "\n" //
			+ "	   if(u_planeClipping == 2 && (gl_FragCoord.x < u_viewport.x || gl_FragCoord.x > u_viewport.z\n" //
			+ "		 || gl_FragCoord.y < u_viewport.w || gl_FragCoord.y > u_viewport.y))" //
			+ "		   discard;" //
			+ "\n" //
			+ "    vec4 tex_color = texture2D(u_texture, v_texCoords);\n"
			+ "    if(tex_color.a == 0.0)\n"	// alpha cut
			+ "		   discard;" //
			+ "	   vec4 src = u_color * v_color * tex_color;\n" //
			+ "	   if(u_fogEnable) {\n" //
			+ "        gl_FragColor = mix(src, vec4(u_fogColor, 1.0), calcFog((gl_FragCoord.z / gl_FragCoord.w) / 64.0));\n" //
			+ "        gl_FragColor.a = src.a;\n" //
			+ "    }\n" //
			+ "    else gl_FragColor = src;\n" //
			+ "}"; //

}
