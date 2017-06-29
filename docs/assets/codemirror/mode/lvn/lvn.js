CodeMirror.defineMode("lvn", function(config, parserConfig) {
  var indentUnit = config.indentUnit;

  function prefixRE(words) {
    return new RegExp("^(?:" + words.join("|") + ")", "i");
  }
  function wordRE(words) {
    return new RegExp("^(?:" + words.join("|") + ")$", "i");
  }
  var specials = wordRE(parserConfig.specials || []);
 
  var builtins = wordRE([
    "_G","_VERSION","assert","collectgarbage","dofile","error","getfenv","getmetatable","ipairs","load",
    "loadfile","loadstring","module","next","pairs","pcall","print","rawequal","rawget","rawset","require",
    "select","setfenv","setmetatable","tonumber","tostring","type","unpack","xpcall",

    "coroutine.create","coroutine.resume","coroutine.running","coroutine.status","coroutine.wrap","coroutine.yield",

    "debug.debug","debug.getfenv","debug.gethook","debug.getinfo","debug.getlocal","debug.getmetatable",
    "debug.getregistry","debug.getupvalue","debug.setfenv","debug.sethook","debug.setlocal","debug.setmetatable",
    "debug.setupvalue","debug.traceback",

    "close","flush","lines","read","seek","setvbuf","write",

    "io.close","io.flush","io.input","io.lines","io.open","io.output","io.popen","io.read","io.stderr","io.stdin",
    "io.stdout","io.tmpfile","io.type","io.write",

    "math.abs","math.acos","math.asin","math.atan","math.atan2","math.ceil","math.cos","math.cosh","math.deg",
    "math.exp","math.floor","math.fmod","math.frexp","math.huge","math.ldexp","math.log","math.log10","math.max",
    "math.min","math.modf","math.pi","math.pow","math.rad","math.random","math.randomseed","math.sin","math.sinh",
    "math.sqrt","math.tan","math.tanh",

    "os.clock","os.date","os.difftime","os.execute","os.exit","os.getenv","os.remove","os.rename","os.setlocale",
    "os.time","os.tmpname",

    "package.cpath","package.loaded","package.loaders","package.loadlib","package.path","package.preload",
    "package.seeall",

    "string.byte","string.char","string.dump","string.find","string.format","string.gmatch","string.gsub",
    "string.len","string.lower","string.match","string.rep","string.reverse","string.sub","string.upper",

    "table.concat","table.insert","table.maxn","table.remove","table.sort"
  ]);
  var keywords = wordRE(["and","break","elseif","false","nil","not","or","return",
			 "true","function", "end", "if", "then", "else", "do", 
			 "while", "repeat", "until", "for", "in", "local" ]);

  var indentTokens = wordRE(["function", "if","repeat","do", "\\(", "{"]);
  var dedentTokens = wordRE(["end", "until", "\\)", "}"]);
  var dedentPartial = prefixRE(["end", "until", "\\)", "}", "else", "elseif"]);

  function normal(stream, state) {
    if (stream.sol()) {
      var ch = stream.peek();
      if (state.codeBlock || ch == "@") {
        var mode = code
        if (ch == "@") {
          stream.next();
          if (stream.peek() == "@") {
            stream.next();
            state.codeBlock = !state.codeBlock;
            if (!state.codeBlock) {
              mode = normal;
            }
          }
        }
        state.cur = mode
        return "code";
      } else if (ch == "#") {
        stream.next();
        if (stream.eat("#")) return (state.cur = commentBlock)(stream, state);
        else return (state.cur = commentLine)(stream, state);
      }
    }    
    
    return (state.cur = textLine)(stream, state);
  }

  function textLine(stream, state) {
    while (!stream.eol()) {
      var ch = stream.peek();
      if (ch == '\\') {
        stream.next();
        stream.next();
      } else if (ch == '[') {
        state.cur = embeddedCode;
        return null;
      } else if (ch == '{') {
        state.cur = textTag;
        return null;		
      } else if (ch == '$') {
        state.cur = internalString;
        return null;
      } else {
        stream.next();
      }
    }
    state.cur = normal;
    return "word";
  }
  
  function commentLine(stream, state) {
    while (!stream.eol()) {
      stream.next();
    }
    state.cur = normal;
    return "comment";
  }

  function commentBlock(stream, state) {
    var ch;
    while ((ch = stream.next()) != null) {
      if (ch == '#' && stream.eat('#')) state.cur = normal;
    }
    return "comment";
  }

  function textTag(stream, state) {
    var style = "texttag";	
    var ch = stream.next();
	
    if (ch == '\\') {
      stream.next();
	} else if (ch == '{') {
	  if (stream.peek() == '/') stream.next();
	  stream.eatWhile(/[\w_]/);
	} else if (ch == '}') {
      state.cur = normal;
    } else if (ch == "\"" || ch == "'") {
      /*style =*/ (state.cur = string(ch, textTag))(stream, state);
    } else if (/\d/.test(ch)) {
      stream.eatWhile(/[\w.%]/);
      //style = "number";
    } else if (/[\w_]/.test(ch)) {
      stream.eatWhile(/[\w_.]/);
      //style = "variable";	  
    }
	
	if (stream.eol()) {
		state.cur = normal;
		//return null;
	}	
    return style;
  }  
  
  function internalString(stream, state) {
    stream.next(); //Skip $
    
    var endChar = ' ';
    var ch = stream.peek();
    if (ch == '{') {
      endChar = '}'
    }
    
    var len = 0;
    while ((ch = stream.next()) != null) {
      if (ch == endChar) break;
      len++;
    }
    
    state.cur = normal;
    if (len <= 0) return null;
    return "stringifier"
  }
  
  
  
  function readBracket(stream) {
    var level = 0;
    while (stream.eat("=")) ++level;
    stream.eat("[");
    return level;
  }
  
  function embeddedCode(stream, state) {
    var style = "code";
    var ch = stream.next();
    if (ch == '\\') {
      stream.next();
    } else if (ch == ']') {
      state.cur = normal;
    } else {
      return code(stream, state);
    }
    return style;
  }
  
  function code(stream, state) {
    var style = "code";
    var ch = stream.next();
        
    if (ch == '\\') {
      stream.next();
    } else if (ch == "-" && stream.eat("-")) {
      if (stream.eat("[")) {
        style = (state.cur = bracketed(readBracket(stream), "comment"))(stream, state);
	  } else {
        stream.skipToEnd();
        style = "comment";
      }
    } else if (ch == "\"" || ch == "'") {
      style = (state.cur = string(ch, code))(stream, state);
    } else if (ch == "[" && /[\[=]/.test(stream.peek())) {
      style = (state.cur = bracketed(readBracket(stream), "string"))(stream, state);
    } else if (/\d/.test(ch)) {
      stream.eatWhile(/[\w.%]/);
      style = "number";
    } else if (/[\w_]/.test(ch)) {
      stream.eatWhile(/[\w_.]/);
      style = "variable";
    }
    
    if (stream.eol()) {
        state.cur = normal;
    }    
    return style;
  }

  function bracketed(level, style) {
    return function(stream, state) {
      var curlev = null, ch;
      while ((ch = stream.next()) != null) {      
        if (ch == '\\') { stream.next(); }
        else if (curlev == null) {if (ch == "]") curlev = 0;}
        else if (ch == "=") ++curlev;
        else if (ch == "]" && curlev == level) { state.cur = code; break; }
        else curlev = null;
      }
      return style;
    };
  }

  function string(quote, returnState) {
    return function(stream, state) {
      var ch;
      while (!stream.eol() && (ch = stream.next()) != null) {
	    if (ch == '\\') stream.next();
        else if (ch == quote) break;
      }
      state.cur = returnState;
      return "string";
    };
  }
    
  return {
    startState: function(basecol) {
      return {basecol: basecol || 0, indentDepth: 0, cur: normal};
    },

    token: function(stream, state) {
      if (stream.eatSpace()) return null;
      var style = state.cur(stream, state);
      var word = stream.current();
      if (style == "number") {
        style = "code";
      }
      if (style == "variable" || style == "code") {
        if (keywords.test(word)) style = "keyword";
        else if (builtins.test(word)) style = "builtin";
		else if (specials.test(word)) style = "variable-2";
        else style = "builtin";
      }
      if ((style != "comment") && (style != "string")){
        if (indentTokens.test(word)) ++state.indentDepth;
        else if (dedentTokens.test(word)) --state.indentDepth;
      }
      return style;
    },

    indent: function(state, textAfter) {
      var closing = dedentPartial.test(textAfter);
      return state.basecol + indentUnit * (state.indentDepth - (closing ? 1 : 0));
    }
  };
});

CodeMirror.defineMIME("text/x-lvn", "lvn");
