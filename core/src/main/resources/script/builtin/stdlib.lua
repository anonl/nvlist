
---Standard library of utility functions
-- @module stdlib

-- ----------------------------------------------------------------------------
-- ----------------------------------------------------------------------------
-- ----------------------------------------------------------------------------

---Takes a list of tables and generates a new table containing SHALLOW copies
-- of all attributes.
function extend(...)
	local result = {}
	for n=1,arg.n do
		local tbl = arg[n]
		if tbl ~= nil then
			for k,v in pairs(tbl) do
				result[k] = v
			end
		end
	end
	return result
end

---Creates a shallow copy of <code>x</code>
function shallowCopy(x)
	if type(x) ~= "table" then
		return x
	end
	return addAll({}, x)
end

---Adds all key/value pairs in <code>values</code> to <code>tbl</code>.
function addAll(tbl, values)
	for k,v in pairs(values) do
		tbl[k] = v
	end
	return tbl
end

---Removes all key/value pairs from <code>tbl</code> for which <code>value == val</code>.
function removeAll(tbl, val)
	local result = {}
	for k,v in pairs(tbl) do
		if v ~= val then
			result[k] = v
		end
	end
	return result
end

---Calls the destroy method on all values of table <code>tbl</code>.
function destroyValues(tbl)
	for _,v in pairs(tbl) do	
		if v ~= nil then
			v:destroy()
		end
	end
end

---Return a new table returning all non-nil values of tbl inserted with sequential numerical indices.
function values(...)
	local result = {}
	local d = 1
	local tbl = getTableOrVarArg(...)
	for _,v in pairs(tbl) do
		if v ~= nil then
			result[d] = v
			d = d + 1
		end
	end
	return result
end

---Returns the sign of the given number (<code>-1</code> for negative values,
-- <code>1</code> for positive, and <code>0</code> for zero).
function signum(x)
	if x > 0 then
		return 1
	elseif x < 0 then
		return -1
	end
	return 0
end

---Converts a table or vararg argument and returns it as a table.
function getTableOrVarArg(...)
	if arg.n == 1 and type(arg[1]) == "table" then
		return arg[1]
	end
	
	local result = {}
	local d = 1
	for s=1,arg.n do
		result[d] = arg[s]
		d = d + 1
	end
	return result
end

---Calls the update method on each argument, then calls <code>join</code>.
-- @see join
function update1join(...)
	local threads = getTableOrVarArg(...)

	for _,thread in ipairs(threads) do
		if not thread:isFinished() then
			thread:update()
		end
	end

	join(threads)
end

---Blocks until all threads passed as an argument are finished.
function join(...)
	local threads = getTableOrVarArg(...)

	while true do
		local finished = true
		for _,thread in ipairs(threads) do
			if not thread:isFinished() then
				finished = false
				break
			end
		end
		if finished then
			break
		end
		yield()
	end
end

---Trims the whitespace from the edges of the given string. 
function trim(str)
	return str:match("^%s*(.-)%s*$")
end

---Splits <code>str</code> based on the regular expression <code>pattern</code>.
function split(str, pattern)
    local result = {}
    local pattern = string.format("([^%s]+)", pattern)
    str:gsub(pattern, function(c) table.insert(result, c) end)
    return result
end

---Returns the script file and line.
-- @param callOffset Offset in the call stack to determine the script file and line of.
function getScriptPos(callOffset)
	callOffset = callOffset or 0

	if debug ~= nil and debug.getinfo ~= nil then
		local info = debug.getinfo(callOffset + 2, 'Sl')
		if info ~= nil then
			return (info.short_src or "undefined") .. ":" .. (info.currentline or 0)
		end
	end
	return "undefined:0"
end

---Returns a 'deep' field from table <code>t</code>. The given <code>name</code>
-- will be split into '.'-separated chunks which are used to recursively
-- traverses tables to find the matching value.
function getDeepField(t, name)
	for _,part in ipairs(split(name, ".")) do
		if t == nil then
			return nil
		end
		t = t[part]
	end
	return t
end

---Returns the value of the local (or upval) variable at the specified level
-- @param level The depth in the callstack to get the locals from (1=current function)
-- @param max The maximum number of local variables to return.
function getLocalVars(level, max)
	level = level or 2
	max = max or 999

	local result = {}
	
	local func = debug.getinfo(level, "f").func
	for i=1,max do
    	local ln, lv = debug.getupvalue(func, i)
	    if ln == nil then
	    	break
	    end
		result[ln] = lv
  	end
	
	for i=1,max do
		local ln, lv = debug.getlocal(level, i)
	    if ln == nil then
	    	break
	    end
		result[ln] = lv
	end
		
	return result
end

-- ----------------------------------------------------------------------------
-- ----------------------------------------------------------------------------
-- ----------------------------------------------------------------------------
