PRAGMA temp_store_directory = '.';

hidden var 'fp7pos' from select jmergeregexp(jgroup(c1)) from (select * from fp7positives order by length(C1) desc) ;
hidden var 'fp7negheavy' from select jmergeregexp(jgroup(c1)) from (select * from fp7strongfilterwords order by length(C1) desc);
hidden var 'fp7neglight' from select jmergeregexp(jgroup(c1)) from (select * from fp7weakfilterwords order by length(C1) desc);
hidden var 'fp7pospos' from select jmergeregexp(jgroup(c1)) from (select * from fp7pospos order by length(C1) desc);
hidden var 'fp7middlepos' from select jmergeregexp(jgroup(c1)) from (select * from fp7positives union select * from fp7pospos union select * from fp7middlepos);
hidden var 'wtnegheavy' from select jmergeregexp(jgroup(c1)) from (select * from wtstrongfilterwords order by length(C1) desc);
hidden var 'wtneglight' from select jmergeregexp(jgroup(c1)) from (select * from wtweakfilterwords order by length(C1) desc);
hidden var 'wtpospos' from select jmergeregexp(jgroup(c1)) from (select * from wtposposwords order by length(C1) desc);

create temp table pubs as setschema 'c1,c2' select jsonpath(c1, '$.id', '$.text') from stdinput();

select jdict('documentId', docid, 'projectId', id, 'confidenceLevel', 0.8) from (
select docid,id from (select docid,upper(regexpr("(\w+.*\d+)",middle)) as match,id,grantid  from (setschema 'docid,prev,middle,next' select c1 as docid,textwindow2s(c2,12,1,5,"(.+\/\w+\/\d{4}\W*\Z)|(\d{6,7})|(\w{2}\d{4,})") from (setschema 'c1,c2' select * from pubs where c2 is not null) ) , grants where match = grantid and (fundingclass1 in ("FCT","ARC") or ( fundingclass1 = "NHMRC" and regexprmatches("nhmrc|medical research|national health medical",filterstopwords(normalizetext(lower(j2s(prev,middle,next)))))))) group by docid,id)

union all 

select jdict('documentId', docid, 'projectId', id, 'confidenceLevel', sqroot(min(1.49,confidence)/1.5)) from ( select docid,id,max(confidence) as confidence from ( select docid, id,
      case when fundingClass1="WT" then /*wellcome trust confidence*/
                (regexpcountwords(var('wtpospos'),j2s(prevpack,nextpack)) * regexpcountwords('(?:collaborative|joint call)',j2s(prevpack,nextpack)))*0.33 +
                regexprmatches('\d{5}ma(?:\b|_)',middle)+
                regexprmatches('(?:\d{5}_)(?:z|c|b|a)(?:_\d{2}_)(?:z|c|b|a)',middle)*2+
                regexpcountwords(var('wtpospos'),prev)*0.5+
                regexprmatches(var('wtpospos'),middle)+
                regexpcountwithpositions(var('wtpospos'),prevpack)*0.39 +
                0.21*regexpcountwithpositions(var('wtpospos'),nextpack,1) -
                (regexprmatches(var('wtnegheavy'),middle) + regexprmatches('(?:n01|r01|dms)',middle) +regexprmatches('(?:ns|mh|hl|hd|ai)(?:_|)\d{5}',middle))*10 -
                4*regexpcountwords('(?:a|g|c|t|u){4}',middle) -
                regexprmatches(var('wtneglight'),middle)*0.3 -
                regexpcountwithpositions(var('wtnegheavy'),prevpacksmall,0,1,0.5)*0.39 -
                regexpcountwithpositions(var('wtneglight'),prevpacksmall,0,1,0.5)*0.18 -
                0.45*regexpcountwithpositions(var('wtneglight'),nextpack) -
                0.21*regexpcountwithpositions(var('wtnegheavy'),nextpack,1) 
       else /* fp7 confidence */
		regexprmatches(var('fp7middlepos'),middle)+
                regexprmatches('(?:\b|_|\d)'||normalizedacro||'(?:\b|_|\d)',j2s(middle,prevpacksmall,nextpack))*2  +
                regexprmatches('fp7',prev15)*0.4 +
                0.4*regexpcountwithpositions(var('fp7pospos'),prevpacksmall) +
                0.16*regexpcountwords(var('fp7pos'),prevpacksmall) +
                0.1*regexpcountwithpositions(var('fp7pospos'),nextpack,1) +
                regexpcountwords(var('fp7pos'),nextpack)*0.04 -
                regexprmatches(var('fp7negheavy'),middle)*1 -
                0.4*regexpcountwords('(a|g|c|t|u){4}',middle) -
                regexprmatches(var('fp7neglight'),middle)*0.3 -
                regexpcountwithpositions(var('fp7negheavy'),prevpacksmall)*0.48 -
                regexpcountwithpositions(var('fp7neglight'),prevpacksmall)*0.18 -
                (((regexpcountwords(('\b_*\d+_*\b'),prevpacksmall)+ (regexpcountwords(('\b_*\d+_*\b'),nextpack)))/4))*0.2 -
                regexpcountwithpositions(var('fp7neglight'),nextpack)*0.03 -
                regexpcountwithpositions(var('fp7negheavy'),nextpack,1)*0.08
       end as confidence
                from
                ( select id,fundingClass1,docid,normalizedacro, j2s(prev14,prev15) as prev,grantid,prev15,j2s(prev1, prev2, prev3, prev4, prev5,prev6,prev7,prev8,prev9,prev10,prev11,prev12,prev13,prev14,prev15) as prevpack ,j2s(prev9,prev10,prev11,prev12,prev13,prev14,prev15) as prevpacksmall , middle, j2s(next1, next2, next3) as nextpack
                    from
                        ( 
                         select * from (setschema 'docid,prev1, prev2, prev3, prev4, prev5, prev6, prev7, prev8, prev9, prev10, prev11, prev12, prev13, prev14, prev15, middle, next1, next2, next3' select  c1 as docid ,textwindow(regexpr('(\b\S*?[^0-9\s_]\S*?\s_?)(\d{3})(\s)(\d{3})(_?\s\S*?[^0-9\s_]\S*?\b)',filterstopwords(normalizetext(lower(c2))),'\1\2\4\5'),15,3,'((?:(?:\b|\D)0|_|\b|\D)(?:\d{5}))|(((\D|\b)\d{6}(\D|\b))) ' )
                            from   (setschema 'c1,c2' select * from  pubs where c2 is not null)) ,grants
                            where  (not regexprmatches( '(?:0|\D|\b)+(?:\d{8,})',middle) and regexpr('(?:0|\D|\b)+(\d{5})',middle) = grantid and fundingclass1  in ('WT', 'EC') ) or ((not regexprmatches('(\d{6,}(?:\d|i\d{3}_?\b))|(jana\d{6,})', middle))
                        and regexpr('(\d{6})',middle) = grantid and fundingclass1 in ('WT', 'EC'))
                        )
                      ) where confidence > 0.16) group by docid,id);
