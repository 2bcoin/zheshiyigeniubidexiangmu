;if("function" == typeof(init)) {
    init.call({});
};
if("function" == typeof(main) && __init()) {
    main.call({}, version);
};
if("function" == typeof(onexit)) {
    onexit.call({});
};
if("function" == typeof(onerror)) {
    onerror.call({});
};